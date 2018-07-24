package com.janeullah.healthinspectionrecords.external.aws;

/**
 * Copyright (c) 2016-2017, Mihai Emil Andronache All rights reserved. Redistribution and use in
 * source and binary forms, with or without modification, are permitted provided that the following
 * conditions are met: 1)Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer. 2)Redistributions in binary form must reproduce
 * the above copyright notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution. 3)Neither the name of
 * charles-rest nor the names of its contributors may be used to endorse or promote products derived
 * from this software without specific prior written permission. THIS SOFTWARE IS PROVIDED BY THE
 * COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT
 * NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED
 * TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
import com.amazonaws.AmazonServiceException;
import com.amazonaws.http.HttpResponse;
import com.amazonaws.http.HttpResponseHandler;

/**
 * https://github.com/opencharles/charles-rest/blob/master/src/main/java/com/amihaiemil/charles/aws/SimpleAwsErrorHandler.java
 * Simple exception handler that returns an {@link AmazonServiceException} containing the HTTP
 * status code and status text.
 *
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 1.0.0
 */
public class SimpleAwsErrorHandler implements HttpResponseHandler<AmazonServiceException> {

  /** See {@link HttpResponseHandler}, method needsConnectionLeftOpen() */
  private boolean needsConnectionLeftOpen;

  /**
   * Ctor.
   *
   * @param connectionLeftOpen Should the connection be closed immediately or not?
   */
  public SimpleAwsErrorHandler(boolean connectionLeftOpen) {
    this.needsConnectionLeftOpen = connectionLeftOpen;
  }

  @Override
  public AmazonServiceException handle(HttpResponse response) {
    AmazonServiceException ase = new AmazonServiceException(response.getStatusText());
    ase.setStatusCode(response.getStatusCode());
    return ase;
  }

  @Override
  public boolean needsConnectionLeftOpen() {
    return this.needsConnectionLeftOpen;
  }
}