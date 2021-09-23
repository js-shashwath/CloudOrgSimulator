package Simulations

import com.typesafe.config.{Config, ConfigFactory}
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.BeforeAndAfter
import org.slf4j.{Logger, LoggerFactory}
import Simulations.MySimulation1

class MySimulation1Test extends AnyFunSuite with BeforeAndAfter {

  val SIM = "my_simulation1";

  val conf: Config = ConfigFactory.load(SIM + ".conf")
  val LOG: Logger = LoggerFactory.getLogger(getClass)

  // check this test case
  test("MySimulation1.createDatacenter") {

    val name = "datacenter"

    val datacenter = MySimulation1.createDatacenter(5)

    val numHosts = conf.getInt(SIM + ".datacenter.numHosts")

    LOG.debug("Testing whether data center got created successfully")
    println(assert(datacenter != null))

    LOG.debug("Testing whether the number of hosts matches the config")
    println(assert(datacenter.getHostList.size() == numHosts))
  }

  test("MySimulation1.createVms") {

    val num_vms = conf.getInt(SIM + ".numVMs")

    // VMs creation call
    val vmlist = MySimulation1.createVms

    LOG.debug("Testing whether vmlist not empty ")
    assert(vmlist.size() != 0)

    LOG.debug("Testing whether number of VMs matches config")
    assert(vmlist.size == num_vms)
  }

  test("MySimulation1.createHost") {

    val num_hosts = conf.getInt(SIM + "." + "datacenter" + ".numHosts")

    // Host creation call
    val host = MySimulation1.createHost

    LOG.debug("Testing whether host not null ")
    assert(host != null)

    LOG.debug("Testing whether host storage matches config")
    assert(host.getStorage.getCapacity == conf.getInt(SIM + "." + "host" + ".storage"))
  }

  test("MySimulation1.createCloudlets") {

    val num_cloudlets =conf.getInt(SIM + "." + "numCloudlets")

    // Cloudlets creation call
    val cloudlets = MySimulation1.createCloudlets

    LOG.debug("Testing whether cloudlets list not empty ")
    assert(cloudlets.size() != 0)

    LOG.debug("Testing whether cloudlets list size matches config")
    assert(cloudlets.size() == num_cloudlets)
  }


}
