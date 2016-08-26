/*
 * Copyright (c) 2016 Cisco and/or its affiliates.
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

package io.fd.honeycomb.infra.distro.data.oper;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import io.fd.honeycomb.data.ReadableDataManager;
import io.fd.honeycomb.data.impl.ReadableDataTreeDelegator;
import io.fd.honeycomb.infra.distro.ProviderTrait;
import io.fd.honeycomb.infra.distro.data.context.ContextPipelineModule;
import io.fd.honeycomb.translate.read.registry.ModifiableReaderRegistryBuilder;
import io.fd.honeycomb.translate.read.registry.ReaderRegistryBuilder;
import org.opendaylight.controller.md.sal.binding.api.DataBroker;
import org.opendaylight.controller.md.sal.binding.impl.BindingToNormalizedNodeCodec;
import org.opendaylight.controller.sal.core.api.model.SchemaService;

public final class ReadableDTDelegProvider extends ProviderTrait<ReadableDataManager> {

    @Inject
    private BindingToNormalizedNodeCodec serializer;
    @Inject
    private SchemaService schemaService;
    @Inject
    private ModifiableReaderRegistryBuilder registry;
    @Inject
    @Named(ContextPipelineModule.HONEYCOMB_CONTEXT)
    private DataBroker contextBroker;

    @Override
    protected ReadableDataTreeDelegator create() {
        return new ReadableDataTreeDelegator(serializer, schemaService.getGlobalContext(),
                ((ReaderRegistryBuilder) registry).build(), contextBroker);
    }
}