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

package io.fd.honeycomb.translate.v3po.interfaces.acl.ingress;

import io.fd.honeycomb.translate.write.WriteFailedException;
import io.fd.vpp.jvpp.core.dto.InputAclSetInterface;
import java.util.List;
import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.access.control.list.rev160708.access.lists.acl.access.list.entries.Ace;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.vpp.acl.rev161214.InterfaceMode;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.vpp.acl.rev161214.ietf.acl.base.attributes.AccessLists;
import org.opendaylight.yangtools.yang.binding.InstanceIdentifier;

/**
 * Writer responsible for translation of ietf-acl model ACEs to VPP's classify tables and sessions.
 */
interface AceWriter {

    /**
     * Translates list of ACEs to chain of classify tables. Each ACE is translated into one classify table with single
     * classify session. Also initializes input_acl_set_interface request message DTO with first classify table of the
     * chain that was created.
     *
     * @param id            uniquely identifies ietf-acl container
     * @param aces          list of access control entries
     * @param mode          interface mode (L2/L3)
     * @param defaultAction to be taken when packet that does not match any of rules defined in
     * @param request       input_acl_set_interface request DTO
     */
    void write(@Nonnull final InstanceIdentifier<?> id, @Nonnull final List<Ace> aces,
               final InterfaceMode mode, final AccessLists.DefaultAction defaultAction,
               @Nonnull final InputAclSetInterface request, @Nonnegative final int vlanTags)
        throws WriteFailedException;
}
