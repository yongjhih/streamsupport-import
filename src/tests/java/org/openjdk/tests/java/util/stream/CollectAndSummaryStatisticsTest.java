/*
 * Copyright (c) 2012, 2017, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

/*
 * @test
 * @summary primtive stream collection with summary statistics
 * @bug 8044047 8178117
 */

package org.openjdk.tests.java.util.stream;

import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import java8.util.DoubleSummaryStatistics;
import java8.util.IntSummaryStatistics;
import java8.util.LongSummaryStatistics;
import java8.util.stream.Collectors;
import java8.util.stream.DoubleStream;
import java8.util.stream.DoubleStreams;
import java8.util.stream.IntStream;
import java8.util.stream.IntStreams;
import java8.util.stream.LongStream;
import java8.util.stream.LongStreams;
import java8.util.stream.OpTestCase;
import java8.util.stream.StreamSupport;

import static java8.util.stream.LambdaTestHelpers.countTo;
import static java8.util.stream.ThrowableHelper.checkNPE;

import static org.testng695.Assert.expectThrows;

@Test
public class CollectAndSummaryStatisticsTest extends OpTestCase {

    public void testIntCollectNull() {
        checkNPE(() -> IntStreams.of(1).collect(null,
                                                IntSummaryStatistics::accept,
                                                IntSummaryStatistics::combine));
        checkNPE(() -> IntStreams.of(1).collect(IntSummaryStatistics::new,
                                                null,
                                                IntSummaryStatistics::combine));
        checkNPE(() -> IntStreams.of(1).collect(IntSummaryStatistics::new,
                                                IntSummaryStatistics::accept,
                                                null));
    }

    public void testLongCollectNull() {
        checkNPE(() -> LongStreams.of(1).collect(null,
                                                 LongSummaryStatistics::accept,
                                                 LongSummaryStatistics::combine));
        checkNPE(() -> LongStreams.of(1).collect(LongSummaryStatistics::new,
                                                 null,
                                                 LongSummaryStatistics::combine));
        checkNPE(() -> LongStreams.of(1).collect(LongSummaryStatistics::new,
                                                 LongSummaryStatistics::accept,
                                                 null));
    }

    public void testDoubleCollectNull() {
        checkNPE(() -> DoubleStreams.of(1).collect(null,
                                                   DoubleSummaryStatistics::accept,
                                                   DoubleSummaryStatistics::combine));
        checkNPE(() -> DoubleStreams.of(1).collect(DoubleSummaryStatistics::new,
                                                   null,
                                                   DoubleSummaryStatistics::combine));
        checkNPE(() -> DoubleStreams.of(1).collect(DoubleSummaryStatistics::new,
                                                   DoubleSummaryStatistics::accept,
                                                   null));
    }

    public void testIntStatistics() {
        List<IntSummaryStatistics> instances = new ArrayList<>();
        instances.add(StreamSupport.stream(countTo(1000)).collect(Collectors.summarizingInt(i -> i)));
        instances.add(StreamSupport.stream(countTo(1000)).mapToInt(i -> i).summaryStatistics());
        instances.add(StreamSupport.stream(countTo(1000)).mapToInt(i -> i).collect(IntSummaryStatistics::new,
                                                                      IntSummaryStatistics::accept,
                                                                      IntSummaryStatistics::combine));
        instances.add(StreamSupport.stream(countTo(1000)).mapToInt(i -> i).collect(() -> new IntSummaryStatistics(0, -1, 1001, 2),
                                                                      IntSummaryStatistics::accept,
                                                                      IntSummaryStatistics::combine));
        instances.add(StreamSupport.parallelStream(countTo(1000)).collect(Collectors.summarizingInt(i -> i)));
        instances.add(StreamSupport.parallelStream(countTo(1000)).mapToInt(i -> i).summaryStatistics());
        instances.add(StreamSupport.parallelStream(countTo(1000)).mapToInt(i -> i).collect(IntSummaryStatistics::new,
                                                                              IntSummaryStatistics::accept,
                                                                              IntSummaryStatistics::combine));
        instances.add(StreamSupport.parallelStream(countTo(1000)).mapToInt(i -> i).collect(() -> new IntSummaryStatistics(0, -1, 1001, 2),
                                                                              IntSummaryStatistics::accept,
                                                                              IntSummaryStatistics::combine));
        IntSummaryStatistics original = instances.get(0);
        instances.add(new IntSummaryStatistics(original.getCount(), original.getMin(), original.getMax(), original.getSum()));

        for (IntSummaryStatistics stats : instances) {
            assertEquals(stats.getCount(), 1000);
            assertEquals(stats.getSum(), StreamSupport.stream(countTo(1000)).mapToInt(i -> i).sum());
            assertEquals(stats.getAverage(), (double) stats.getSum() / stats.getCount());
            assertEquals(stats.getMax(), 1000);
            assertEquals(stats.getMin(), 1);
        }

        expectThrows(IllegalArgumentException.class, () -> new IntSummaryStatistics(-1, 0, 0, 0));
        expectThrows(IllegalArgumentException.class, () -> new IntSummaryStatistics(1, 3, 2, 0));
    }


    public void testLongStatistics() {
        List<LongSummaryStatistics> instances = new ArrayList<>();
        instances.add(StreamSupport.stream(countTo(1000)).collect(Collectors.summarizingLong(i -> i)));
        instances.add(StreamSupport.stream(countTo(1000)).mapToLong(i -> i).summaryStatistics());
        instances.add(StreamSupport.stream(countTo(1000)).mapToLong(i -> i).collect(LongSummaryStatistics::new,
                                                                       LongSummaryStatistics::accept,
                                                                       LongSummaryStatistics::combine));
        instances.add(StreamSupport.stream(countTo(1000)).mapToInt(i -> i).collect(() -> new LongSummaryStatistics(0, -1, 1001, 2),
                                                                      LongSummaryStatistics::accept,
                                                                      LongSummaryStatistics::combine));
        instances.add(StreamSupport.parallelStream(countTo(1000)).collect(Collectors.summarizingLong(i -> i)));
        instances.add(StreamSupport.parallelStream(countTo(1000)).mapToLong(i -> i).summaryStatistics());
        instances.add(StreamSupport.parallelStream(countTo(1000)).mapToLong(i -> i).collect(LongSummaryStatistics::new,
                                                                               LongSummaryStatistics::accept,
                                                                               LongSummaryStatistics::combine));
        instances.add(StreamSupport.parallelStream(countTo(1000)).mapToInt(i -> i).collect(() -> new LongSummaryStatistics(0, -1, 1001, 2),
                                                                              LongSummaryStatistics::accept,
                                                                              LongSummaryStatistics::combine));
        LongSummaryStatistics original = instances.get(0);
        instances.add(new LongSummaryStatistics(original.getCount(), original.getMin(), original.getMax(), original.getSum()));

        for (LongSummaryStatistics stats : instances) {
            assertEquals(stats.getCount(), 1000);
            assertEquals(stats.getSum(), (long) StreamSupport.stream(countTo(1000)).mapToInt(i -> i).sum());
            assertEquals(stats.getAverage(), (double) stats.getSum() / stats.getCount());
            assertEquals(stats.getMax(), 1000L);
            assertEquals(stats.getMin(), 1L);
        }

        expectThrows(IllegalArgumentException.class, () -> new LongSummaryStatistics(-1, 0, 0, 0));
        expectThrows(IllegalArgumentException.class, () -> new LongSummaryStatistics(1, 3, 2, 0));
    }

    public void testDoubleStatistics() {
        List<DoubleSummaryStatistics> instances = new ArrayList<>();
        instances.add(StreamSupport.stream(countTo(1000)).collect(Collectors.summarizingDouble(i -> i)));
        instances.add(StreamSupport.stream(countTo(1000)).mapToDouble(i -> i).summaryStatistics());
        instances.add(StreamSupport.stream(countTo(1000)).mapToDouble(i -> i).collect(DoubleSummaryStatistics::new,
                                                                         DoubleSummaryStatistics::accept,
                                                                         DoubleSummaryStatistics::combine));
        instances.add(StreamSupport.stream(countTo(1000)).mapToInt(i -> i).collect(() -> new DoubleSummaryStatistics(0, -1, 1001, 2),
                                                                      DoubleSummaryStatistics::accept,
                                                                      DoubleSummaryStatistics::combine));
        instances.add(StreamSupport.parallelStream(countTo(1000)).collect(Collectors.summarizingDouble(i -> i)));
        instances.add(StreamSupport.parallelStream(countTo(1000)).mapToDouble(i -> i).summaryStatistics());
        instances.add(StreamSupport.parallelStream(countTo(1000)).mapToDouble(i -> i).collect(DoubleSummaryStatistics::new,
                                                                                 DoubleSummaryStatistics::accept,
                                                                                 DoubleSummaryStatistics::combine));
        instances.add(StreamSupport.parallelStream(countTo(1000)).mapToInt(i -> i).collect(() -> new DoubleSummaryStatistics(0, -1, 1001, 2),
                                                                              DoubleSummaryStatistics::accept,
                                                                              DoubleSummaryStatistics::combine));
        DoubleSummaryStatistics original = instances.get(0);
        instances.add(new DoubleSummaryStatistics(original.getCount(), original.getMin(), original.getMax(), original.getSum()));

        for (DoubleSummaryStatistics stats : instances) {
            assertEquals(stats.getCount(), 1000);
            assertEquals(stats.getSum(), (double) StreamSupport.stream(countTo(1000)).mapToInt(i -> i).sum());
            assertEquals(stats.getAverage(), stats.getSum() / stats.getCount());
            assertEquals(stats.getMax(), 1000.0);
            assertEquals(stats.getMin(), 1.0);
        }

        expectThrows(IllegalArgumentException.class, () -> new DoubleSummaryStatistics(-1, 0, 0, 0));
        expectThrows(IllegalArgumentException.class, () -> new DoubleSummaryStatistics(1, 3, 2, 0));
        double[] values = {1.0, Double.NaN};
        for (double min : values) {
            for (double max : values) {
                for (double sum : values) {
                    if (Double.isNaN(min) && Double.isNaN(max) && Double.isNaN(sum)) continue;
                    if (!Double.isNaN(min) && !Double.isNaN(max) && !Double.isNaN(sum)) continue;
                    expectThrows(IllegalArgumentException.class, () -> new DoubleSummaryStatistics(1, min, max, sum));
                }
            }
        }
    }
}
