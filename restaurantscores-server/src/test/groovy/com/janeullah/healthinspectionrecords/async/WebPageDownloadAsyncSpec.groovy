package com.janeullah.healthinspectionrecords.async


import com.janeullah.healthinspectionrecords.domain.PathVariables
import com.janeullah.healthinspectionrecords.services.external.NorthEastGeorgiaWebPageDownloadService
import com.janeullah.healthinspectionrecords.util.TestFileUtil
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

import java.nio.charset.Charset
import java.util.concurrent.CompletableFuture

//https://junit.org/junit4/javadoc/latest/org/junit/rules/TemporaryFolder.html
//https://blog.andresteingress.com/2014/07/22/spock-junit-rules
class WebPageDownloadAsyncSpec extends Specification {

    def pathVariables = Mock(PathVariables)
    def downloadService = Mock(NorthEastGeorgiaWebPageDownloadService)
    def webPageDownloadAsync = new WebPageDownloadAsync(pathVariables, downloadService)

    @Rule
    TemporaryFolder temporaryFolder

    def "successful download & write of the web page" () {
        setup:
        String htmlFile = TestFileUtil.readFile("src/test/resources/downloads/webpages/Barrow_county_restaurant_scores.html", Charset.forName("UTF-8"));
        File tempFile = temporaryFolder.newFile("sample_text.txt")

        downloadService.getWebPage(_ as String) >> htmlFile
        pathVariables.getDefaultFilePath(_ as String) >> tempFile

        expect:
        CompletableFuture<Boolean> result = webPageDownloadAsync.downloadWebPage("Clarke");
        result.get()
        result.isDone()
        !result.isCompletedExceptionally()
        htmlFile == TestFileUtil.readFile(tempFile.toPath(), Charset.forName("UTF-8"))
    }

    def "successful undownload of the web page" () {
        setup:

        downloadService.getWebPage(_ as String) >> { throw new IOException()}

        expect:
        CompletableFuture<Boolean> result = webPageDownloadAsync.downloadWebPage("Clarke");
        !result.get()
        result.isDone()
        !result.isCompletedExceptionally()
    }
}
