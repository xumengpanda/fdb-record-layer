/*
 * MapWhileCursorTest.java
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

package com.apple.foundationdb.record.cursors;

import com.apple.foundationdb.record.RecordCursor;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Tests for {@link MapWhileCursor}.
 */
public class MapWhileCursorTest {
    static final List<Integer> ints = Arrays.asList(1, 2, 3, 4, 5);

    @Test
    public void noStop() {
        RecordCursor<Integer> cursor = newCursor(i -> i > 5, null, MapWhileCursor.StopContinuation.NONE, RecordCursor.NoNextReason.SCAN_LIMIT_REACHED);
        assertEquals(Arrays.asList(2, 3, 4, 5, 6), cursor.asList().join());
        assertNull(cursor.getContinuation());
        assertEquals(RecordCursor.NoNextReason.SOURCE_EXHAUSTED, cursor.getNoNextReason());
    }

    @Test
    public void stopAndContinue() {
        RecordCursor<Integer> cursor = newCursor(i -> i > 2, null, MapWhileCursor.StopContinuation.AFTER, RecordCursor.NoNextReason.SCAN_LIMIT_REACHED);
        assertEquals(Arrays.asList(2, 3), cursor.asList().join());
        assertNotNull(cursor.getContinuation());
        assertEquals(RecordCursor.NoNextReason.SCAN_LIMIT_REACHED, cursor.getNoNextReason());
        cursor = newCursor(i -> i > 5, cursor.getContinuation(), MapWhileCursor.StopContinuation.AFTER, RecordCursor.NoNextReason.SCAN_LIMIT_REACHED);
        assertEquals(Arrays.asList(5, 6), cursor.asList().join());
        assertNull(cursor.getContinuation());
        assertEquals(RecordCursor.NoNextReason.SOURCE_EXHAUSTED, cursor.getNoNextReason());
    }

    @Test
    public void stopAndRepeat() {
        RecordCursor<Integer> cursor = newCursor(i -> i > 2, null, MapWhileCursor.StopContinuation.BEFORE, RecordCursor.NoNextReason.SCAN_LIMIT_REACHED);
        assertEquals(Arrays.asList(2, 3), cursor.asList().join());
        assertNotNull(cursor.getContinuation());
        assertEquals(RecordCursor.NoNextReason.SCAN_LIMIT_REACHED, cursor.getNoNextReason());
        cursor = newCursor(i -> i > 4, cursor.getContinuation(), MapWhileCursor.StopContinuation.BEFORE, RecordCursor.NoNextReason.SCAN_LIMIT_REACHED);
        assertEquals(Arrays.asList(4, 5), cursor.asList().join());
        assertNotNull(cursor.getContinuation());
        assertEquals(RecordCursor.NoNextReason.SCAN_LIMIT_REACHED, cursor.getNoNextReason());
        cursor = newCursor(i -> i > 10, cursor.getContinuation(), MapWhileCursor.StopContinuation.BEFORE, RecordCursor.NoNextReason.SCAN_LIMIT_REACHED);
        assertEquals(Arrays.asList(6), cursor.asList().join());
        assertNull(cursor.getContinuation());
        assertEquals(RecordCursor.NoNextReason.SOURCE_EXHAUSTED, cursor.getNoNextReason());
    }

    private RecordCursor<Integer> newCursor(Predicate<Integer> stopCondition, byte[] continuation, MapWhileCursor.StopContinuation stopContinuation, RecordCursor.NoNextReason noNextReason) {
        return new MapWhileCursor<>(
                RecordCursor.fromList(ints, continuation),
                i -> stopCondition.test(i) ? Optional.empty() : Optional.of(i + 1),
                stopContinuation, continuation, noNextReason);
    }

}
