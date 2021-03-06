/*
 * Copyright 2017-2018 Uber Technologies, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.uber.h3core;

import com.uber.h3core.util.Vector2D;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Tests for JNI code without going through {@link H3Core}.
 */
public class TestNativeMethods {
    /**
     * Test that h3SetToLinkedGeo properly propagates an exception
     */
    @Test
    public void testH3SetToLinkedGeoException() throws IOException {
        NativeMethods nativeMethods = H3CoreLoader.loadNatives();

        final AtomicInteger counter = new AtomicInteger(0);

        try {
            nativeMethods.h3SetToLinkedGeo(new long[]{0x8928308280fffffL}, new ArrayList<List<List<Vector2D>>>() {
                @Override
                public boolean add(List<List<Vector2D>> lists) {
                    throw new RuntimeException("crashed#" + counter.getAndIncrement());
                }
            });
            assertTrue(false);
        } catch (RuntimeException ex) {
            assertEquals("crashed#0", ex.getMessage());
        }
        assertEquals(1, counter.get());
    }
}
