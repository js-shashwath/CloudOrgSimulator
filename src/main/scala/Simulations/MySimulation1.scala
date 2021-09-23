package Simulations

import java.util
import com.typesafe.config.{Config, ConfigFactory}
import org.cloudbus.cloudsim.allocationpolicies.VmAllocationPolicyFirstFit
import org.cloudbus.cloudsim.brokers.DatacenterBrokerSimple
import org.cloudbus.cloudsim.cloudlets.{Cloudlet, CloudletSimple}
import org.cloudbus.cloudsim.core.CloudSim
import org.cloudbus.cloudsim.datacenters.DatacenterSimple
import org.cloudbus.cloudsim.hosts.{Host, HostSimple}
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple
import org.cloudbus.cloudsim.resources.{Pe, PeSimple}
import org.cloudbus.cloudsim.schedulers.cloudlet.CloudletSchedulerTimeShared
import org.cloudbus.cloudsim.schedulers.vm.VmSchedulerTimeShared
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelFull
import org.cloudbus.cloudsim.vms.{Vm, VmSimple}
import org.cloudsimplus.builders.tables.{CloudletsTableBuilder, TextTableColumn}
import org.slf4j.{Logger, LoggerFactory}

import scala.jdk.CollectionConverters.*
import scala.jdk.javaapi.CollectionConverters.asJava

/**
 * A custom example using CloudSim Plus framework which has a predefined DataCenter configuration
 * having First Fit VM Allocation Policy, Full Cloudlet Utilization Model, Space-Shared
 * Cloudlet Scheduler Type, Time-Shared VM Scheduler Type
 *
 */
object MySimulation1 {

  val SIM = "my_simulation1"
  val conf: Config = ConfigFactory.load(SIM + ".conf")
  val HOSTS: Int = conf.getInt(SIM + "." + "datacenter" + ".numHosts")
  val HOST_PES: Int = conf.getInt(SIM + "." + "host" + ".pes")
  val VMS: Int = conf.getInt(SIM + "." + "numVMs")
  val VM_PES: Int = conf.getInt(SIM + "." + "vm" + ".pesNumber")
  val CLOUDLETS: Int = conf.getInt(SIM + "." + "numCloudlets")
  val CLOUDLET_PES: Int = conf.getInt(SIM + "." + "cloudlet" + ".pesNumber")
  val CLOUDLET_LENGTH: Int = conf.getInt(SIM + "." + "cloudlet" + ".length")

  val LOG: Logger = LoggerFactory.getLogger(getClass)

  def Start() = {}
  val simulation = new CloudSim
  val broker0 = new DatacenterBrokerSimple(simulation)
  val datacenter: DatacenterSimple = createDatacenter(HOSTS)

  val vmList: util.List[Vm] = createVms
  val cloudletList: util.List[CloudletSimple] = createCloudlets

  broker0.submitVmList(vmList)
  broker0.submitCloudletList(cloudletList)
  simulation.start()
  val finishedCloudlets: util.List[Cloudlet] = broker0.getCloudletFinishedList
  new CloudletsTableBuilder(finishedCloudlets).addColumn(new TextTableColumn("Actual CPU Time"), (cloudlet: Cloudlet) => cloudlet.getActualCpuTime).addColumn(new TextTableColumn("Total Cost"), (cloudlet: Cloudlet) => cloudlet.getTotalCost).build()
  LOG.info("The Simulation has ended.")

  /**
   * Creates a Datacenter and its Hosts.
   */
  def createDatacenter(numHosts: Int): DatacenterSimple = {
    val hostList_new = (1 to numHosts).map(host => createHost).toList

    val dc = new DatacenterSimple(simulation, hostList_new.asJava, new VmAllocationPolicyFirstFit)
    dc.getCharacteristics
      .setCostPerBw(conf.getInt(SIM + "." + "datacenter" + ".costPerBw"))
      .setCostPerMem(conf.getInt(SIM + "." + "datacenter" + ".costPerMem"))
      .setCostPerSecond(conf.getInt(SIM + "." + "datacenter" + ".cost"))
      .setCostPerStorage(conf.getInt(SIM + "." + "datacenter" + ".costPerStorage"))
    dc
  }

  /**
   * Creates a list of Hosts (Physical entities to which VMs will be allocated to be run inside Data centers).
   */
  def createHost: Host = {

    val peList = (1 to HOST_PES).map(pe => new PeSimple(1000)).toList
    val ram = conf.getInt(SIM + "." + "host" + ".ram")
    val bw = conf.getInt(SIM + "." + "host" + ".bw")
    val storage = conf.getInt(SIM + "." + "host" + ".storage")
    new HostSimple(ram, bw, storage, asJava[Pe](peList)).setVmScheduler(new VmSchedulerTimeShared())
  }

  /**
   * Creates a list of VMs.
   */
  def createVms: util.List[Vm] = {
    val list = (1 to VMS).map(vm => new VmSimple(1000, VM_PES).setCloudletScheduler(new CloudletSchedulerTimeShared)).toList
    list.asJava
  }

  /**
   * Creates a list of Cloudlets.
   */
  def createCloudlets: util.List[CloudletSimple] = {
    // Uses the Full Utilization Model meaning a Cloudlet always utilizes a given allocated resource from its Vm at 100%, all the time.
    val utilizationModel = new UtilizationModelFull
    val list = (1 to CLOUDLETS).map(c => new CloudletSimple(CLOUDLET_LENGTH, CLOUDLET_PES, utilizationModel)).toList
    list.asJava
  }

  def main(args: Array[String]): Unit = {
    MySimulation1
  }
}
