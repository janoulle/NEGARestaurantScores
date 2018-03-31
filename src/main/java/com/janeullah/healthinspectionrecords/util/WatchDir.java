package com.janeullah.healthinspectionrecords.util;

/*
 * Copyright (c) 2008, 2010, Oracle and/or its affiliates. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Oracle nor the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import com.google.common.collect.Maps;
import com.janeullah.healthinspectionrecords.constants.WebPageConstants;
import com.janeullah.healthinspectionrecords.events.WebPageProcessing;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.concurrent.ConcurrentMap;

import static java.nio.file.LinkOption.NOFOLLOW_LINKS;
import static java.nio.file.StandardWatchEventKinds.*;

/**
 * Example to watch a directory (or tree) for changes to files.
 * https://docs.oracle.com/javase/tutorial/essential/io/notification.html
 */
@Slf4j
public class WatchDir {

    private static ConcurrentMap<String, Boolean> entriesBeingWatched = Maps.newConcurrentMap();
    private final WatchService watcher;
    private final ConcurrentMap<WatchKey, Path> keys;
    private final boolean recursive;
    private final Path dir;
    private boolean trace = false;
    private WebPageProcessing webPageProcessing;
    //private PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:*.{html,text}");

    /**
     * Creates a WatchService and registers the given directory
     */
    public WatchDir(Path dir, boolean recursive) throws IOException {
        this.watcher = FileSystems.getDefault().newWatchService();
        this.keys = Maps.newConcurrentMap();
        this.recursive = recursive;
        this.dir = dir;

        if (recursive) {
            log.info(String.format("event=\"Scanning %s ...%n\"", dir));
            registerAll(dir);
            log.info("event=\"scanning done\"");
        } else {
            register(dir);
        }

        // enable trace after initial registration
        this.trace = true;
    }

    @SuppressWarnings("unchecked")
    static <T> WatchEvent<T> cast(WatchEvent<?> event) {
        return (WatchEvent<T>) event;
    }

    public static WatchEvent.Kind[] getWatchableEvents() {
        try {
            String[] eventsToWatchFor = WebPageConstants.WATCHABLE_EVENTS.split(",");
            if (eventsToWatchFor.length > 0) {
                WatchEvent.Kind[] events = new WatchEvent.Kind[eventsToWatchFor.length];
                for (int i = 0; i < eventsToWatchFor.length; i++) {
                    if ("ENTRY_CREATE".equalsIgnoreCase(eventsToWatchFor[i])) {
                        events[i] = ENTRY_CREATE;
                    } else if ("ENTRY_MODIFY".equalsIgnoreCase(eventsToWatchFor[i])) {
                        events[i] = ENTRY_MODIFY;
                    } else if ("ENTRY_DELETE".equalsIgnoreCase(eventsToWatchFor[i])) {
                        events[i] = ENTRY_DELETE;
                    }
                }
                return events;
            }
        } catch (Exception e) {
            log.error("Error retrieving watchable events", e);
        }
        return new WatchEvent.Kind[]{ENTRY_CREATE};
    }

    /**
     * Returns dir if already set. otherwise, returns path in system property 'PATH_TO_PAGE_STORAGE'
     *
     * @return Path
     */
    public Path getPath() {
        if (dir == null) {
            return Paths.get(WebPageConstants.PATH_TO_PAGE_STORAGE);
        }
        return dir;
    }

    /**
     * Register the given directory with the WatchService
     */
    private void register(Path dir) throws IOException {
        WatchKey key = dir.register(watcher, getWatchableEvents());
        if (trace) {
            Path prev = keys.get(key);
            if (prev == null) {
                log.info(String.format("register_path=\"%s%n\"", dir));
            } else {
                if (!dir.equals(prev)) {
                    log.info(String.format("update_path=\"%s -> %s%n\"", prev, dir));
                }
            }
        }
        keys.put(key, dir);
    }

    /**
     * Register the given directory, and all its sub-directories, with the
     * WatchService.
     */
    private void registerAll(final Path start) throws IOException {
        // register directory and sub-directories
        Files.walkFileTree(start, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs)
                    throws IOException {
                register(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    public void executeProcess() {
        for (; ; ) {

            // wait for key to be signaled
            WatchKey key;
            try {
                key = watcher.take();
            } catch (InterruptedException x) {
                return;
            }

            for (WatchEvent<?> event : key.pollEvents()) {
                WatchEvent.Kind kind = event.kind();

                if (kind == OVERFLOW) {
                    continue;
                }

                //The filename is the context of the event.
                WatchEvent<Path> ev = cast(event);
                Path filename = ev.context();

                //Verify that the new file is a text file.
                try {
                    //String contentType = Files.probeContentType(child);
                    //if (!contentType.equals(MediaType.HTML_UTF_8.toString())) {
                    Path child = dir.resolve(filename);
                    String friendlyname = FilenameUtils.getName(filename.getFileName().toString());
                    if (StringUtils.isNotBlank(friendlyname) && friendlyname.endsWith(".html")) {
                        boolean hasTaskKickedOff = entriesBeingWatched.get(friendlyname) != null && entriesBeingWatched.get(friendlyname);
                        if (hasTaskKickedOff) {
                            log.info(String.format("event=\"async task has already been kicked off for filename %s", filename));
                        } else {
                            log.info(String.format("event=\"async task is being kicked off for filename %s", filename));
                            webPageProcessing.asyncProcessFile(child);
                        }
                    }
                } catch (Exception x) {
                    log.error("Error processing triggered watchable event", x);
                }
            }

            //Reset the key -- this step is critical if you want to receive
            //further watch events. If the key is no longer valid, the directory
            //is inaccessible so exit the loop.
            boolean valid = key.reset();
            if (!valid) {
                keys.remove(key);
                break;
            }
        }
    }

    /**
     * Process all events for keys queued to the watcher
     */
    public void processEventsDefault() {
        for (; ; ) {

            // wait for key to be signalled
            WatchKey key;
            try {
                key = watcher.take();
            } catch (InterruptedException x) {
                return;
            }

            Path dir = keys.get(key);
            if (dir == null) {
                System.err.println("WatchKey not recognized!!");
                continue;
            }

            for (WatchEvent<?> event : key.pollEvents()) {
                WatchEvent.Kind kind = event.kind();

                // TBD - provide example of how OVERFLOW event is handled
                if (kind == OVERFLOW) {
                    continue;
                }

                // Context for directory entry event is the file name of entry
                WatchEvent<Path> ev = cast(event);
                Path name = ev.context();
                Path child = dir.resolve(name);

                // print out event
                System.out.format("%s: %s%n", event.kind().name(), child);

                // if directory is created, and watching recursively, then
                // register it and its sub-directories
                if (recursive && (kind == ENTRY_CREATE)) {
                    try {
                        if (Files.isDirectory(child, NOFOLLOW_LINKS)) {
                            registerAll(child);
                        }
                    } catch (IOException x) {
                        // ignore to keep sample readbale
                    }
                }
            }

            // reset key and remove from set if directory no longer accessible
            boolean valid = key.reset();
            if (!valid) {
                keys.remove(key);

                // all directories are inaccessible
                if (keys.isEmpty()) {
                    break;
                }
            }
        }
    }

    @Autowired
    public void setWebPageProcessing(WebPageProcessing webPageProcessing) {
        this.webPageProcessing = webPageProcessing;
    }
}
