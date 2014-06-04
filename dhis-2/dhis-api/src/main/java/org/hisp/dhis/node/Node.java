package org.hisp.dhis.node;

/*
 * Copyright (c) 2004-2014, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE
 */

import java.util.List;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
public interface Node
{
    /**
     * Name of this node.
     *
     * @return current name of node
     */
    String getName();

    /**
     * Type specifier for this node.
     *
     * @return Node type
     * @see org.hisp.dhis.node.NodeType
     */
    NodeType getType();

    /**
     * Namespace for this node. Not all serializers support this, and its up to the
     * NodeSerializer implementation to decide what to do with this.
     *
     * @return namespace
     * @see org.hisp.dhis.node.NodeSerializer
     */
    String getNamespace();

    /**
     * Comment for this node. Not all serializers support this, and its up to the
     * NodeSerializer implementation to decide what to do with this.
     *
     * @return namespace
     * @see org.hisp.dhis.node.NodeSerializer
     */
    String getComment();

    /**
     * Adds a child to this node.
     *
     * @param child Child node to add
     * @return Child node that was added
     */
    <T extends Node> T addChild( T child );

    /**
     * Adds a collection of children to this node.
     *
     * @param children Child nodes to add
     */
    <T extends Node> void addChildren( Iterable<T> children );

    List<Node> getChildren();
}