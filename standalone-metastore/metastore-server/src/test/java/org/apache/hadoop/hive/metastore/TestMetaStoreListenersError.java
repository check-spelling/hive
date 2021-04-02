/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.hadoop.hive.metastore;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hive.metastore.annotation.MetastoreUnitTest;
import org.apache.hadoop.hive.metastore.api.MetaException;
import org.apache.hadoop.hive.metastore.conf.MetastoreConf;
import org.apache.hadoop.hive.metastore.security.HadoopThriftAuthBridge;

import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

/**
 * Test for unwrapping InvocationTargetException, which is thrown from
 * constructor of listener class
 */
@Category(MetastoreUnitTest.class)
public class TestMetaStoreListenersError {

  @Test
  public void testInitListenerException() throws Throwable {

    System.setProperty("hive.metastore.init.hooks", ErrorInitListener.class.getName());
    Configuration conf = MetastoreConf.newMetastoreConf();
    MetaStoreTestUtils.setConfForStandaloneMode(conf);
    int port = MetaStoreTestUtils.findFreePort();
    try {
      HiveMetaStore.startMetaStore(port, HadoopThriftAuthBridge.getBridge(), conf);
      Assert.fail();
    } catch (Throwable throwable) {
      Assert.assertEquals(MetaException.class, throwable.getClass());
      Assert.assertEquals(
          "Failed to instantiate listener named: " +
              "org.apache.hadoop.hive.metastore.TestMetaStoreListenersError$ErrorInitListener, " +
              "reason: java.lang.IllegalArgumentException: exception on constructor",
          throwable.getMessage());
    }
  }

  @Test
  public void testEventListenerException() throws Throwable {

    System.setProperty("hive.metastore.init.hooks", "");
    System.setProperty("hive.metastore.event.listeners", ErrorEventListener.class.getName());
    Configuration conf = MetastoreConf.newMetastoreConf();
    MetaStoreTestUtils.setConfForStandaloneMode(conf);
    int port = MetaStoreTestUtils.findFreePort();
    try {
      HiveMetaStore.startMetaStore(port, HadoopThriftAuthBridge.getBridge(), conf);
      Assert.fail();
    } catch (Throwable throwable) {
      Assert.assertEquals(MetaException.class, throwable.getClass());
      Assert.assertEquals(
          "Failed to instantiate listener named: " +
              "org.apache.hadoop.hive.metastore.TestMetaStoreListenersError$ErrorEventListener, " +
              "reason: java.lang.IllegalArgumentException: exception on constructor",
          throwable.getMessage());
    }
  }

  public static class ErrorInitListener extends MetaStoreInitListener {

    public ErrorInitListener(Configuration config) {
      super(config);
      throw new IllegalArgumentException("exception on constructor");
    }

    public void onInit(MetaStoreInitContext context) throws MetaException {
    }
  }

  public static class ErrorEventListener extends MetaStoreEventListener {

    public ErrorEventListener(Configuration config) {
      super(config);
      throw new IllegalArgumentException("exception on constructor");
    }
  }
}
