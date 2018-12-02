/*
 * IndexMaintainerState.java
 *
 * This source file is part of the FoundationDB open source project
 *
 * Copyright 2015-2018 Apple Inc. and the FoundationDB project authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.apple.foundationdb.record.provider.foundationdb;

import com.apple.foundationdb.API;
import com.apple.foundationdb.Transaction;
import com.apple.foundationdb.record.metadata.Index;
import com.apple.foundationdb.subspace.Subspace;
import com.google.protobuf.Message;

import javax.annotation.Nonnull;

/**
 * Common state for an {@link IndexMaintainer}.
 *
 * In particular, this includes the record store and index meta-data for the maintainer.
 *
 * @param <M> type used to represent stored records
 */
@API(API.Status.MAINTAINED)
public class IndexMaintainerState<M extends Message> {
    @Nonnull
    public final FDBRecordStoreBase<M> store;
    @Nonnull
    public final FDBRecordContext context;
    @Nonnull
    public final Index index;
    @Nonnull
    public final Subspace indexSubspace;
    @Nonnull
    public final Transaction transaction;
    @Nonnull
    public final IndexMaintenanceFilter filter;

    public IndexMaintainerState(@Nonnull FDBRecordStoreBase<M> store, @Nonnull FDBRecordContext context,
                                @Nonnull Index index, @Nonnull Subspace indexSubspace, @Nonnull Transaction transaction,
                                @Nonnull IndexMaintenanceFilter filter) {
        this.store = store;
        this.context = context;
        this.index = index;
        this.indexSubspace = indexSubspace;
        this.transaction = transaction;
        this.filter = filter;
    }

    public IndexMaintainerState(@Nonnull FDBRecordStoreBase<M> store,
                                @Nonnull Index index,
                                @Nonnull IndexMaintenanceFilter filter) {
        this(store, store.getContext(), index, store.getUntypedRecordStore().indexSubspace(index), store.getContext().ensureActive(), filter);
    }
}
