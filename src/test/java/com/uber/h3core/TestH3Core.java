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

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.uber.h3core.exceptions.PentagonEncounteredException;
import com.uber.h3core.util.Vector2D;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class TestH3Core {
    public double EPSILON = 1e-6;

    private static H3Core h3;

    @BeforeClass
    public static void setup() throws IOException {
        h3 = H3Core.newInstance();
    }

    @Test
    public void testConstructAnother() throws IOException {
        assertNotNull(h3);

        H3Core another = H3Core.newInstance();

        // Doesn't override equals.
        assertNotEquals(h3, another);
    }

    @Test
    public void testH3IsValid() {
        assertTrue(h3.h3IsValid(22758474429497343L | (1L << 59L)));
        assertFalse(h3.h3IsValid(-1L));
        assertTrue(h3.h3IsValid("8f28308280f18f2"));
    }

    @Test
    public void testGeoToH3() {
        assertEquals(h3.geoToH3(67.194013596, 191.598258018, 5), 22758474429497343L | (1L << 59L));
    }

    @Test
    public void testH3ToGeo() {
        Vector2D coords = h3.h3ToGeo(22758474429497343L | (1L << 59L));
        assertEquals(coords.x, 67.15092686397713, EPSILON);
        assertEquals(coords.y, 191.6091114190303 - 360.0, EPSILON);

        Vector2D coords2 = h3.h3ToGeo(Long.toHexString(22758474429497343L | (1L << 59L)));
        assertEquals(coords, coords2);
    }

    @Test
    public void testH3ToGeoBoundary() {
        List<Vector2D> boundary = h3.h3ToGeoBoundary(22758474429497343L | (1L << 59L));
        List<Vector2D> actualBoundary = new ArrayList<>();
        actualBoundary.add(new Vector2D(67.224749856, 191.476993415 - 360.0));
        actualBoundary.add(new Vector2D(67.140938355, 191.373085667 - 360.0));
        actualBoundary.add(new Vector2D(67.067252558, 191.505086715 - 360.0));
        actualBoundary.add(new Vector2D(67.077062918, 191.740304069 - 360.0));
        actualBoundary.add(new Vector2D(67.160561948, 191.845198829 - 360.0));
        actualBoundary.add(new Vector2D(67.234563187, 191.713897218 - 360.0));

        for (int i = 0; i < 6; i++) {
            assertEquals(boundary.get(i).x, actualBoundary.get(i).x, EPSILON);
            assertEquals(boundary.get(i).y, actualBoundary.get(i).y, EPSILON);
        }

        List<Vector2D> boundary2 = h3.h3ToGeoBoundary(Long.toHexString(22758474429497343L | (1L << 59L)));
        assertEquals(boundary, boundary2);
    }

    @Test
    public void testKring() {
        List<String> hexagons = h3.kRing("8928308280fffff", 1);

        assertEquals(1 + 6, hexagons.size());

        assertTrue(hexagons.contains("8928308280fffff"));
        assertTrue(hexagons.contains("8928308280bffff"));
        assertTrue(hexagons.contains("89283082807ffff"));
        assertTrue(hexagons.contains("89283082877ffff"));
        assertTrue(hexagons.contains("89283082803ffff"));
        assertTrue(hexagons.contains("89283082873ffff"));
        assertTrue(hexagons.contains("8928308283bffff"));
    }

    @Test
    public void testKring2() {
        List<String> hexagons = h3.kRing("8928308280fffff", 2);

        assertEquals(1 + 6 + 12, hexagons.size());

        assertTrue(hexagons.contains("89283082813ffff"));
        assertTrue(hexagons.contains("89283082817ffff"));
        assertTrue(hexagons.contains("8928308281bffff"));
        assertTrue(hexagons.contains("89283082863ffff"));
        assertTrue(hexagons.contains("89283082823ffff"));
        assertTrue(hexagons.contains("89283082873ffff"));
        assertTrue(hexagons.contains("89283082877ffff"));
        assertTrue(hexagons.contains("8928308287bffff"));
        assertTrue(hexagons.contains("89283082833ffff"));
        assertTrue(hexagons.contains("8928308282bffff"));
        assertTrue(hexagons.contains("8928308283bffff"));
        assertTrue(hexagons.contains("89283082857ffff"));
        assertTrue(hexagons.contains("892830828abffff"));
        assertTrue(hexagons.contains("89283082847ffff"));
        assertTrue(hexagons.contains("89283082867ffff"));
        assertTrue(hexagons.contains("89283082803ffff"));
        assertTrue(hexagons.contains("89283082807ffff"));
        assertTrue(hexagons.contains("8928308280bffff"));
        assertTrue(hexagons.contains("8928308280fffff"));
    }

    @Test
    public void testKring1And2() {
        List<List<String>> kRings = h3.kRings("8928308280fffff", 2);

        List<String> hexagons;
        hexagons = kRings.get(1);

        assertEquals(1 + 6, hexagons.size());

        assertTrue(hexagons.contains("8928308280fffff"));
        assertTrue(hexagons.contains("8928308280bffff"));
        assertTrue(hexagons.contains("89283082807ffff"));
        assertTrue(hexagons.contains("89283082877ffff"));
        assertTrue(hexagons.contains("89283082803ffff"));
        assertTrue(hexagons.contains("89283082873ffff"));
        assertTrue(hexagons.contains("8928308283bffff"));

        hexagons = kRings.get(2);
        assertEquals(1 + 6 + 12, hexagons.size());

        assertTrue(hexagons.contains("89283082813ffff"));
        assertTrue(hexagons.contains("89283082817ffff"));
        assertTrue(hexagons.contains("8928308281bffff"));
        assertTrue(hexagons.contains("89283082863ffff"));
        assertTrue(hexagons.contains("89283082823ffff"));
        assertTrue(hexagons.contains("89283082873ffff"));
        assertTrue(hexagons.contains("89283082877ffff"));
        assertTrue(hexagons.contains("8928308287bffff"));
        assertTrue(hexagons.contains("89283082833ffff"));
        assertTrue(hexagons.contains("8928308282bffff"));
        assertTrue(hexagons.contains("8928308283bffff"));
        assertTrue(hexagons.contains("89283082857ffff"));
        assertTrue(hexagons.contains("892830828abffff"));
        assertTrue(hexagons.contains("89283082847ffff"));
        assertTrue(hexagons.contains("89283082867ffff"));
        assertTrue(hexagons.contains("89283082803ffff"));
        assertTrue(hexagons.contains("89283082807ffff"));
        assertTrue(hexagons.contains("8928308280bffff"));
        assertTrue(hexagons.contains("8928308280fffff"));
    }

    @Test
    public void testKringLarge() {
        int k = 50;
        List<String> hexagons = h3.kRing("8928308280fffff", k);

        int expectedCount = 1;
        for (int i = 1; i <= k; i++) {
            expectedCount += i * 6;
        }

        assertEquals(expectedCount, hexagons.size());
    }

    @Test
    public void testKringPentagon() {
        List<String> hexagons = h3.kRing("821c07fffffffff", 1);

        assertEquals(1 + 5, hexagons.size());

        assertTrue(hexagons.contains("821c2ffffffffff"));
        assertTrue(hexagons.contains("821c27fffffffff"));
        assertTrue(hexagons.contains("821c07fffffffff"));
        assertTrue(hexagons.contains("821c17fffffffff"));
        assertTrue(hexagons.contains("821c1ffffffffff"));
        assertTrue(hexagons.contains("821c37fffffffff"));
    }

    @Test
    public void testHexRange() throws PentagonEncounteredException {
        List<List<String>> hexagons = h3.hexRange("8928308280fffff", 1);

        assertEquals(2, hexagons.size());
        assertEquals(1, hexagons.get(0).size());
        assertEquals(6, hexagons.get(1).size());

        assertTrue(hexagons.get(0).contains("8928308280fffff"));
        assertTrue(hexagons.get(1).contains("8928308280bffff"));
        assertTrue(hexagons.get(1).contains("89283082807ffff"));
        assertTrue(hexagons.get(1).contains("89283082877ffff"));
        assertTrue(hexagons.get(1).contains("89283082803ffff"));
        assertTrue(hexagons.get(1).contains("89283082873ffff"));
        assertTrue(hexagons.get(1).contains("8928308283bffff"));
    }

    @Test
    public void testKRingDistances() {
        List<List<String>> hexagons = h3.kRingDistances("8928308280fffff", 1);

        assertEquals(2, hexagons.size());
        assertEquals(1, hexagons.get(0).size());
        assertEquals(6, hexagons.get(1).size());

        assertTrue(hexagons.get(0).contains("8928308280fffff"));
        assertTrue(hexagons.get(1).contains("8928308280bffff"));
        assertTrue(hexagons.get(1).contains("89283082807ffff"));
        assertTrue(hexagons.get(1).contains("89283082877ffff"));
        assertTrue(hexagons.get(1).contains("89283082803ffff"));
        assertTrue(hexagons.get(1).contains("89283082873ffff"));
        assertTrue(hexagons.get(1).contains("8928308283bffff"));
    }

    @Test
    public void testHexRange2() {
        List<List<String>> hexagons = h3.kRingDistances("8928308280fffff", 2);

        assertEquals(3, hexagons.size());
        assertEquals(1, hexagons.get(0).size());
        assertEquals(6, hexagons.get(1).size());
        assertEquals(12, hexagons.get(2).size());

        assertTrue(hexagons.get(0).contains("8928308280fffff"));
        assertTrue(hexagons.get(1).contains("8928308280bffff"));
        assertTrue(hexagons.get(1).contains("89283082873ffff"));
        assertTrue(hexagons.get(1).contains("89283082877ffff"));
        assertTrue(hexagons.get(1).contains("8928308283bffff"));
        assertTrue(hexagons.get(1).contains("89283082807ffff"));
        assertTrue(hexagons.get(1).contains("89283082803ffff"));
        assertTrue(hexagons.get(2).contains("8928308281bffff"));
        assertTrue(hexagons.get(2).contains("89283082857ffff"));
        assertTrue(hexagons.get(2).contains("89283082847ffff"));
        assertTrue(hexagons.get(2).contains("8928308287bffff"));
        assertTrue(hexagons.get(2).contains("89283082863ffff"));
        assertTrue(hexagons.get(2).contains("89283082867ffff"));
        assertTrue(hexagons.get(2).contains("8928308282bffff"));
        assertTrue(hexagons.get(2).contains("89283082823ffff"));
        assertTrue(hexagons.get(2).contains("89283082833ffff"));
        assertTrue(hexagons.get(2).contains("892830828abffff"));
        assertTrue(hexagons.get(2).contains("89283082817ffff"));
        assertTrue(hexagons.get(2).contains("89283082813ffff"));
    }

    @Test
    public void testKRingDistancesPentagon() {
        h3.kRingDistances("821c07fffffffff", 1);
        // No exception should happen
    }

    @Test(expected = PentagonEncounteredException.class)
    public void testHexRangePentagon() throws PentagonEncounteredException {
        h3.hexRange("821c07fffffffff", 1);
    }

    @Test
    public void testPolyfill() {
        List<Long> hexagons = h3.polyfill(
                ImmutableList.of(
                        new Vector2D(37.813318999983238, -122.4089866999972145),
                        new Vector2D(37.7866302000007224, -122.3805436999997056),
                        new Vector2D(37.7198061999978478, -122.3544736999993603),
                        new Vector2D(37.7076131999975672, -122.5123436999983966),
                        new Vector2D(37.7835871999971715, -122.5247187000021967),
                        new Vector2D(37.8151571999998453, -122.4798767000009008)
                ), null, 9
        );

        assertTrue(hexagons.size() > 1000);
    }

    @Test
    public void testPolyfillAddresses() {
        List<String> hexagons = h3.polyfillAddress(
                ImmutableList.<Vector2D>of(
                        new Vector2D(37.813318999983238, -122.4089866999972145),
                        new Vector2D(37.7866302000007224, -122.3805436999997056),
                        new Vector2D(37.7198061999978478, -122.3544736999993603),
                        new Vector2D(37.7076131999975672, -122.5123436999983966),
                        new Vector2D(37.7835871999971715, -122.5247187000021967),
                        new Vector2D(37.8151571999998453, -122.4798767000009008)
                ), null, 9
        );

        assertTrue(hexagons.size() > 1000);
    }

    @Test
    public void testPolyfillWithHole() {
        List<Long> hexagons = h3.polyfill(
                ImmutableList.<Vector2D>of(
                        new Vector2D(37.813318999983238, -122.4089866999972145),
                        new Vector2D(37.7866302000007224, -122.3805436999997056),
                        new Vector2D(37.7198061999978478, -122.3544736999993603),
                        new Vector2D(37.7076131999975672, -122.5123436999983966),
                        new Vector2D(37.7835871999971715, -122.5247187000021967),
                        new Vector2D(37.8151571999998453, -122.4798767000009008)
                ),
                ImmutableList.<List<Vector2D>>of(
                        ImmutableList.<Vector2D>of(
                                new Vector2D(37.7869802, -122.4471197),
                                new Vector2D(37.7664102, -122.4590777),
                                new Vector2D(37.7710682, -122.4137097)
                        )
                ),
                9
        );

        assertTrue(hexagons.size() > 1000);
    }

    @Test
    public void testPolyfillWithTwoHoles() {
        List<Long> hexagons = h3.polyfill(
                ImmutableList.<Vector2D>of(
                        new Vector2D(37.813318999983238, -122.4089866999972145),
                        new Vector2D(37.7866302000007224, -122.3805436999997056),
                        new Vector2D(37.7198061999978478, -122.3544736999993603),
                        new Vector2D(37.7076131999975672, -122.5123436999983966),
                        new Vector2D(37.7835871999971715, -122.5247187000021967),
                        new Vector2D(37.8151571999998453, -122.4798767000009008)
                ),
                ImmutableList.<List<Vector2D>>of(
                        ImmutableList.<Vector2D>of(
                                new Vector2D(37.7869802, -122.4471197),
                                new Vector2D(37.7664102, -122.4590777),
                                new Vector2D(37.7710682, -122.4137097)
                        ),
                        ImmutableList.<Vector2D>of(
                                new Vector2D(37.747976, -122.490025),
                                new Vector2D(37.731550, -122.503758),
                                new Vector2D(37.725440, -122.452603)
                        )
                ),
                9
        );

        assertTrue(hexagons.size() > 1000);
    }

    @Test
    public void testPolyfillKnownHoles() {
        List<Long> inputHexagons = h3.kRing(0x85283083fffffffL, 2);
        inputHexagons.remove(0x8528308ffffffffL);
        inputHexagons.remove(0x85283097fffffffL);
        inputHexagons.remove(0x8528309bfffffffL);

        List<List<Vector2D>> geo = h3.h3SetToMultiPolygon(inputHexagons, true).get(0);

        // TODO: looks like a bug in H3 that this is index 1
        List<Vector2D> outline = geo.remove(1); // geo is now holes

        List<Long> outputHexagons = h3.polyfill(outline, geo, 5);

        assertEquals(ImmutableSet.copyOf(inputHexagons), ImmutableSet.copyOf(outputHexagons));
    }

    @Test
    public void testH3SetToMultiPolygonEmpty() {
        assertEquals(0, h3.h3SetToMultiPolygon(new ArrayList<Long>(), false).size());
    }

    @Test
    public void testH3SetToMultiPolygonSingle() {
        long testIndex = 0x89283082837ffffL;

        List<Vector2D> actualBounds = h3.h3ToGeoBoundary(testIndex);
        List<List<List<Vector2D>>> multiBounds = h3.h3SetToMultiPolygon(ImmutableList.of(testIndex), true);

        // This is tricky, because output in an order starting from any vertex
        // would also be correct, but that's difficult to assert and there's
        // value in being specific here

        assertEquals(1, multiBounds.size());
        assertEquals(1, multiBounds.get(0).size());
        assertEquals(actualBounds.size() + 1, multiBounds.get(0).get(0).size());

        int[] expectedIndices = {2, 3, 4, 5, 0, 1, 2};

        for (int i = 0; i < actualBounds.size(); i++) {
            assertEquals(actualBounds.get(expectedIndices[i]).x, multiBounds.get(0).get(0).get(i).x, EPSILON);
            assertEquals(actualBounds.get(expectedIndices[i]).y, multiBounds.get(0).get(0).get(i).y, EPSILON);
        }
    }

    @Test
    public void testH3SetToMultiPolygonSingleNonGeoJson() {
        long testIndex = 0x89283082837ffffL;

        List<Vector2D> actualBounds = h3.h3ToGeoBoundary(testIndex);
        List<List<List<Vector2D>>> multiBounds = h3.h3SetToMultiPolygon(ImmutableList.of(testIndex), false);

        // This is tricky, because output in an order starting from any vertex
        // would also be correct, but that's difficult to assert and there's
        // value in being specific here

        assertEquals(1, multiBounds.size());
        assertEquals(1, multiBounds.get(0).size());
        assertEquals(actualBounds.size(), multiBounds.get(0).get(0).size());

        int[] expectedIndices = {2, 3, 4, 5, 0, 1};

        for (int i = 0; i < actualBounds.size(); i++) {
            assertEquals(actualBounds.get(expectedIndices[i]).y, multiBounds.get(0).get(0).get(i).x, EPSILON);
            assertEquals(actualBounds.get(expectedIndices[i]).x, multiBounds.get(0).get(0).get(i).y, EPSILON);
        }
    }

    @Test
    public void testH3SetToMultiPolygonContiguous2() {
        long testIndex = 0x89283082837ffffL;
        long testIndex2 = 0x89283082833ffffL;

        List<Vector2D> actualBounds = h3.h3ToGeoBoundary(testIndex);
        List<Vector2D> actualBounds2 = h3.h3ToGeoBoundary(testIndex2);

        // Note this is different than the h3core-js bindings, in that it uses GeoJSON (possible bug)
        List<List<List<Vector2D>>> multiBounds = h3.h3SetToMultiPolygon(ImmutableList.of(testIndex, testIndex2), false);

        assertEquals(1, multiBounds.size());
        assertEquals(1, multiBounds.get(0).size());
        assertEquals(10, multiBounds.get(0).get(0).size());

        assertEquals(actualBounds2.get(0).y, multiBounds.get(0).get(0).get(0).x, EPSILON);
        assertEquals(actualBounds2.get(1).y, multiBounds.get(0).get(0).get(1).x, EPSILON);
        assertEquals(actualBounds2.get(2).y, multiBounds.get(0).get(0).get(2).x, EPSILON);
        assertEquals(actualBounds.get(1).y, multiBounds.get(0).get(0).get(3).x, EPSILON);
        assertEquals(actualBounds.get(2).y, multiBounds.get(0).get(0).get(4).x, EPSILON);
        assertEquals(actualBounds.get(3).y, multiBounds.get(0).get(0).get(5).x, EPSILON);
        assertEquals(actualBounds.get(4).y, multiBounds.get(0).get(0).get(6).x, EPSILON);
        assertEquals(actualBounds.get(5).y, multiBounds.get(0).get(0).get(7).x, EPSILON);
        assertEquals(actualBounds2.get(4).y, multiBounds.get(0).get(0).get(8).x, EPSILON);
        assertEquals(actualBounds2.get(5).y, multiBounds.get(0).get(0).get(9).x, EPSILON);
        assertEquals(actualBounds2.get(0).x, multiBounds.get(0).get(0).get(0).y, EPSILON);
        assertEquals(actualBounds2.get(1).x, multiBounds.get(0).get(0).get(1).y, EPSILON);
        assertEquals(actualBounds2.get(2).x, multiBounds.get(0).get(0).get(2).y, EPSILON);
        assertEquals(actualBounds.get(1).x, multiBounds.get(0).get(0).get(3).y, EPSILON);
        assertEquals(actualBounds.get(2).x, multiBounds.get(0).get(0).get(4).y, EPSILON);
        assertEquals(actualBounds.get(3).x, multiBounds.get(0).get(0).get(5).y, EPSILON);
        assertEquals(actualBounds.get(4).x, multiBounds.get(0).get(0).get(6).y, EPSILON);
        assertEquals(actualBounds.get(5).x, multiBounds.get(0).get(0).get(7).y, EPSILON);
        assertEquals(actualBounds2.get(4).x, multiBounds.get(0).get(0).get(8).y, EPSILON);
        assertEquals(actualBounds2.get(5).x, multiBounds.get(0).get(0).get(9).y, EPSILON);
    }

    @Test
    public void testH3SetToMultiPolygonNonContiguous2() {
        long testIndex = 0x89283082837ffffL;
        long testIndex2 = 0x8928308280fffffL;

        List<List<List<Vector2D>>> multiBounds = h3.h3SetToMultiPolygon(ImmutableList.of(testIndex, testIndex2), false);

        // TODO: Update to appropriate expectations when the algorithm correctly
        // returns two polygons

        assertEquals(1, multiBounds.size());
        assertEquals(2, multiBounds.get(0).size());
        assertEquals(6, multiBounds.get(0).get(0).size());
        assertEquals(6, multiBounds.get(0).get(1).size());
    }

    @Test
    public void testH3SetToMultiPolygonHole() {
        // Six hexagons in a ring around a hole
        List<List<List<Vector2D>>> multiBounds = h3.h3AddressSetToMultiPolygon(ImmutableList.of(
                "892830828c7ffff", "892830828d7ffff", "8928308289bffff",
                "89283082813ffff", "8928308288fffff", "89283082883ffff"
        ), false);

        assertEquals(1, multiBounds.size());
        assertEquals(2, multiBounds.get(0).size());
        assertEquals(6 * 3, multiBounds.get(0).get(0).size());
        assertEquals(6, multiBounds.get(0).get(1).size());
    }

    @Test
    public void testH3SetToMultiPolygonLarge() {
        int numHexes = 20000;

        List<String> addresses = new ArrayList<>();
        for (int i = 0; i < numHexes; i++) {
            addresses.add(h3.geoToH3Address(i * 0.01, 0, 15));
        }

        // Six hexagons in a ring around a hole
        List<List<List<Vector2D>>> multiBounds = h3.h3AddressSetToMultiPolygon(addresses, false);

        assertEquals(numHexes, multiBounds.get(0).size());
    }

    @Test
    public void testHexRing() throws PentagonEncounteredException {
        List<String> hexagons = h3.hexRing("8928308280fffff", 1);

        assertEquals(6, hexagons.size());

        assertTrue(hexagons.contains("8928308280bffff"));
        assertTrue(hexagons.contains("89283082807ffff"));
        assertTrue(hexagons.contains("89283082877ffff"));
        assertTrue(hexagons.contains("89283082803ffff"));
        assertTrue(hexagons.contains("89283082873ffff"));
        assertTrue(hexagons.contains("8928308283bffff"));
    }

    @Test
    public void testHexRing2() throws PentagonEncounteredException {
        List<String> hexagons = h3.hexRing("8928308280fffff", 2);

        assertEquals(12, hexagons.size());

        assertTrue(hexagons.contains("89283082813ffff"));
        assertTrue(hexagons.contains("89283082817ffff"));
        assertTrue(hexagons.contains("8928308281bffff"));
        assertTrue(hexagons.contains("89283082863ffff"));
        assertTrue(hexagons.contains("89283082823ffff"));
        assertTrue(hexagons.contains("8928308287bffff"));
        assertTrue(hexagons.contains("89283082833ffff"));
        assertTrue(hexagons.contains("8928308282bffff"));
        assertTrue(hexagons.contains("89283082857ffff"));
        assertTrue(hexagons.contains("892830828abffff"));
        assertTrue(hexagons.contains("89283082847ffff"));
        assertTrue(hexagons.contains("89283082867ffff"));
    }

    @Test
    public void testHexRingLarge() throws PentagonEncounteredException {
        int k = 50;
        List<String> hexagons = h3.hexRing("8928308280fffff", k);

        int expectedCount = 50 * 6;

        assertEquals(expectedCount, hexagons.size());
    }

    @Test(expected = PentagonEncounteredException.class)
    public void testHexRingPentagon() throws PentagonEncounteredException {
        h3.hexRing("821c07fffffffff", 1);
    }

    @Test(expected = PentagonEncounteredException.class)
    public void testHexRingAroundPentagon() throws PentagonEncounteredException {
        h3.hexRing("821c37fffffffff", 2);
    }

    @Test
    public void testHostileInput() {
        assertNotEquals(0, h3.geoToH3(-987654321, 987654321, 5));
        assertNotEquals(0, h3.geoToH3(987654321, -987654321, 5));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testHostileGeoToH3NaN() {
        h3.geoToH3(Double.NaN, Double.NaN, 5);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testHostileGeoToH3PositiveInfinity() {
        h3.geoToH3(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, 5);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testHostileGeoToH3NegativeInfinity() {
        h3.geoToH3(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, 5);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testHostileInputNegativeRes() {
        h3.geoToH3(0, 0, -1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testHostileInputLargeRes() {
        h3.geoToH3(0, 0, 1000);
    }

    @Test
    public void testHostileInputLatLng() {
        try {
            assertEquals(0x80effffffffffffL, h3.geoToH3(1e45, 1e45, 0));
        } catch (IllegalArgumentException e) {
            // Also acceptable result
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testHostileInputMaximum() {
        h3.geoToH3(Double.MAX_VALUE, Double.MAX_VALUE, 0);
    }

    @Test
    public void testH3GetResolution() {
        assertEquals(0, h3.h3GetResolution(0x8029fffffffffffL));
        assertEquals(15, h3.h3GetResolution(0x8f28308280f18f2L));
        assertEquals(14, h3.h3GetResolution(0x8e28308280f18f7L));
        assertEquals(9, h3.h3GetResolution("8928308280fffff"));

        // These are invalid, we're checking for not crashing.
        assertEquals(0, h3.h3GetResolution(0));
        assertEquals(15, h3.h3GetResolution(0xffffffffffffffffL));
    }

    @Test
    public void testH3ToParent() {
        assertEquals(0x801dfffffffffffL, h3.h3ToParent(0x811d7ffffffffffL, 0));
        assertEquals(0x801dfffffffffffL, h3.h3ToParent(0x801dfffffffffffL, 0));
        assertEquals(0x8828308281fffffL, h3.h3ToParent(0x8928308280fffffL, 8));
        assertEquals(0x872830828ffffffL, h3.h3ToParent(0x8928308280fffffL, 7));
        assertEquals("872830828ffffff", h3.h3ToParentAddress("8928308280fffff", 7));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testH3ToParentInvalidRes() {
        h3.h3ToParent(0, 5);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testH3ToParentInvalid() {
        h3.h3ToParent(0x8928308280fffffL, -1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testH3ToParentInvalid2() {
        h3.h3ToParent(0x8928308280fffffL, 17);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testH3ToParentInvalid3() {
        h3.h3ToParent(0, 17);
    }

    @Test
    public void testH3ToChildren() {
        List<String> sfChildren = h3.h3ToChildren("88283082803ffff", 9);

        assertEquals(7, sfChildren.size());
        assertTrue(sfChildren.contains("8928308280fffff"));
        assertTrue(sfChildren.contains("8928308280bffff"));
        assertTrue(sfChildren.contains("8928308281bffff"));
        assertTrue(sfChildren.contains("89283082813ffff"));
        assertTrue(sfChildren.contains("89283082817ffff"));
        assertTrue(sfChildren.contains("89283082807ffff"));
        assertTrue(sfChildren.contains("89283082803ffff"));

        List<Long> pentagonChildren = h3.h3ToChildren(0x801dfffffffffffL, 2);

        // res 0 pentagon has 5 hexagon children and 1 pentagon child at res 1.
        // Total output will be:
        //   5 * 7 children of res 1 hexagons
        //   6 children of res 1 pentagon
        assertEquals(5 * 7 + 6, pentagonChildren.size());

        // Don't crash
        h3.h3ToChildren(0, 2);
        try {
            h3.h3ToChildren("88283082803ffff", -1);
            assertTrue(false);
        } catch (IllegalArgumentException ex) {
            // expected
        }
        try {
            h3.h3ToChildren("88283082803ffff", 17);
            assertTrue(false);
        } catch (IllegalArgumentException ex) {
            // expected
        }
    }

    @Test
    public void testH3IsResClassIII() {
        String r0 = h3.geoToH3Address(0, 0, 0);
        String r1 = h3.geoToH3Address(10, 0, 1);
        String r2 = h3.geoToH3Address(0, 10, 2);
        String r3 = h3.geoToH3Address(10, 10, 3);

        assertFalse(h3.h3IsResClassIII(r0));
        assertTrue(h3.h3IsResClassIII(r1));
        assertFalse(h3.h3IsResClassIII(r2));
        assertTrue(h3.h3IsResClassIII(r3));
    }

    @Test
    public void testConstants() {
        double lastAreaKm2 = 0;
        double lastAreaM2 = 0;
        double lastEdgeLengthKm = 0;
        double lastEdgeLengthM = 0;
        long lastNumHexagons = Long.MAX_VALUE;
        for (int i = 15; i >= 0; i--) {
            double areaKm2 = h3.hexArea(i, AreaUnit.km2);
            double areaM2 = h3.hexArea(i, AreaUnit.m2);
            double edgeKm = h3.edgeLength(i, LengthUnit.km);
            double edgeM = h3.edgeLength(i, LengthUnit.m);
            long numHexagons = h3.numHexagons(i);

            assertTrue(areaKm2 > lastAreaKm2);
            assertTrue(areaM2 > lastAreaM2);
            assertTrue(areaM2 > areaKm2);
            assertTrue(edgeKm > lastEdgeLengthKm);
            assertTrue(edgeM > lastEdgeLengthM);
            assertTrue(edgeM > edgeKm);
            assertTrue(numHexagons < lastNumHexagons);
            assertTrue(numHexagons > 0);

            lastAreaKm2 = areaKm2;
            lastAreaM2 = areaM2;
            lastEdgeLengthKm = edgeKm;
            lastEdgeLengthM = edgeM;
            lastNumHexagons = numHexagons;
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstantsInvalid() {
        h3.hexArea(-1, AreaUnit.km2);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstantsInvalid2() {
        h3.hexArea(0, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstantsInvalid3() {
        h3.edgeLength(0, null);
    }

    @Test
    public void testH3GetBaseCell() {
        assertEquals(20, h3.h3GetBaseCell("8f28308280f18f2"));
        assertEquals(20, h3.h3GetBaseCell(0x8f28308280f18f2L));
        assertEquals(14, h3.h3GetBaseCell("821c07fffffffff"));
        assertEquals(14, h3.h3GetBaseCell(0x821c07fffffffffL));
    }

    @Test
    public void testH3IsPentagon() {
        assertFalse(h3.h3IsPentagon("8f28308280f18f2"));
        assertFalse(h3.h3IsPentagon(0x8f28308280f18f2L));
        assertTrue(h3.h3IsPentagon("821c07fffffffff"));
        assertTrue(h3.h3IsPentagon(0x821c07fffffffffL));
    }

    @Test
    public void testCompact() {
        // Some random location
        String starting = h3.geoToH3Address(30, 20, 6);

        Collection<String> expanded = h3.kRing(starting, 8);

        Collection<String> compacted = h3.compactAddress(expanded);

        // Visually inspected the results to determine this was OK.
        assertEquals(61, compacted.size());

        Collection<String> uncompacted = h3.uncompactAddress(compacted, 6);

        assertEquals(expanded.size(), uncompacted.size());

        // Assert contents are the same
        assertEquals(new HashSet<>(expanded), new HashSet<>(uncompacted));
    }

    @Test(expected = RuntimeException.class)
    public void testCompactInvalid() {
        // Some random location
        String starting = h3.geoToH3Address(30, 20, 6);

        List<String> expanded = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            expanded.add(starting);
        }

        h3.compactAddress(expanded);
    }

    @Test
    public void testUncompactPentagon() {
        List<String> addresses = h3.uncompactAddress(ImmutableList.of("821c07fffffffff"), 3);
        assertEquals(6, addresses.size());
        addresses.stream()
                .forEach(h -> assertEquals(3, h3.h3GetResolution(h)));
    }

    @Test(expected = RuntimeException.class)
    public void testUncompactInvalid() {
        h3.uncompactAddress(ImmutableList.of("0"), 3);
    }

    @Test
    public void testUnidirectionalEdges() {
        String start = "891ea6d6533ffff";
        String adjacent = "891ea6d65afffff";
        String notAdjacent = "891ea6992dbffff";

        assertTrue(h3.h3IndexesAreNeighbors(start, adjacent));
        assertFalse(h3.h3IndexesAreNeighbors(start, notAdjacent));
        // Indexes are not considered to neighbor themselves
        assertFalse(h3.h3IndexesAreNeighbors(start, start));

        String edge = h3.getH3UnidirectionalEdge(start, adjacent);

        assertTrue(h3.h3UnidirectionalEdgeIsValid(edge));
        assertFalse(h3.h3UnidirectionalEdgeIsValid(start));

        assertEquals(start, h3.getOriginH3IndexFromUnidirectionalEdge(edge));
        assertEquals(adjacent, h3.getDestinationH3IndexFromUnidirectionalEdge(edge));

        List<String> components = h3.getH3IndexesFromUnidirectionalEdge(edge);
        assertEquals(2, components.size());
        assertEquals(start, components.get(0));
        assertEquals(adjacent, components.get(1));

        Collection<String> edges = h3.getH3UnidirectionalEdgesFromHexagon(start);
        assertEquals(6, edges.size());
        assertTrue(edges.contains(edge));

        List<Vector2D> boundary = h3.getH3UnidirectionalEdgeBoundary(edge);
        assertEquals(2, boundary.size());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUnidirectionalEdgesNotNeighbors() {
        h3.getH3UnidirectionalEdge("891ea6d6533ffff", "891ea6992dbffff");
    }
}
