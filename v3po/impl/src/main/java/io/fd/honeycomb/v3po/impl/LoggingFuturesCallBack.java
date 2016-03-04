/*
 * Copyright (c) 2015 Cisco and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.fd.honeycomb.v3po.impl;

import com.google.common.util.concurrent.FutureCallback;
import org.slf4j.Logger;

public class LoggingFuturesCallBack<V> implements FutureCallback<V> {

    private final Logger log;
    private final String message;

    public LoggingFuturesCallBack(final String message, final Logger log) {
        this.message = message;
        this.log = log;
    }

    @Override
    public void onFailure(final Throwable err) {
        log.warn(message,err);
    }

    @Override
    public void onSuccess(final V arg0) {
        /* suppress success messages
        if (arg0 == null) {
            LOG.info("Success!");
        } else {
            LOG.info("Success! {}", arg0);
        }
        */
    }
}