package Simulations

import java.util
import com.typesafe.config.{Config, ConfigFactory}
import org.cloudbus.cloudsim.allocationpolicies.{VmAllocationPolicyBestFit, VmAllocationPolicyRandom}
import org.cloudbus.cloudsim.brokers.DatacenterBrokerSimple
import org.cloudbus.cloudsim.cloudlets.{Cloudlet, CloudletSimple}
import org.cloudbus.cloudsim.core.CloudSim
import org.cloudbus.cloudsim.datacenters.DatacenterSimple
import org.cloudbus.cloudsim.distributions.PoissonDistr
import org.cloudbus.cloudsim.hosts.{Host, HostSimple}
import org.cloudbus.cloudsim.resources.{Pe, PeSimple}
import org.cloudbus.cloudsim.schedulers.cloudlet.CloudletSchedulerTimeShared
import org.cloudbus.cloudsim.schedulers.vm.VmSchedulerSpaceShared
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelDynamic
import org.cloudbus.cloudsim.vms.{Vm, VmSimple}
import org.cloudsimplus.builders.tables.{CloudletsTableBuilder, TextTableColumn}
import org.slf4j.{Logger, LoggerFactory}

import scala.jdk.CollectionConverters.*
import scala.jdk.javaapi.CollectionConverters.asJava

/**
 * An cloud model implementation using CloudSim Plus framework which has a predefined DataCenter configuration having the below policies:
 * - Best Fit VM Allocation Policy
 * - Dynamic Cloudlet Utilization Model (set to 25% utilization)
 * - Time-Shared Cloudlet Scheduler Type
 * - Space-Shared VM Scheduler Type
 */

object MySimulation2 {

  val SIM = "my_simulation2"
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
  val datacenter0: DatacenterSimple = createDatacenter(HOSTS)

  val vmList: util.List[Vm] = createVms
  val cloudletList: util.List[CloudletSimple] = createCloudlets

  broker0.submitVmList(vmList)
  broker0.submitCloudletList(cloudletList)
  simulation.start
  val finishedCloudlets: util.List[Cloudlet] = broker0.getCloudletFinishedList
  new CloudletsTableBuilder(finishedCloudlets).addColumn(new TextTableColumn("Actual CPU Time"), (cloudlet: Cloudlet) => cloudlet.getActualCpuTime).addColumn(new TextTableColumn("Total Cost"), (cloudlet: Cloudlet) => cloudlet.getTotalCost).build()
  LOG.info("The Simulation has ended.")

  /**
   * Creates a Datacenter and its Hosts using the Best fit VM allocation policy.
   */
  def createDatacenter(numHosts: Int): DatacenterSimple = {
    val hostList_new = (1 to numHosts).map(host => createHost).toList

    val dc = new DatacenterSimple(simulation, hostList_new.asJava, new VmAllocationPolicyBestFit)
    dc.getCharacteristics
      .setCostPerBw(conf.getInt(SIM + "." + "datacenter" + ".costPerBw"))
      .setCostPerMem(conf.getInt(SIM + "." + "datacenter" + ".costPerMem"))
      .setCostPerSecond(conf.getInt(SIM + "." + "datacenter" + ".cost"))
      .setCostPerStorage(conf.getInt(SIM + "." + "datacenter" + ".costPerStorage"))
    dc
  }

  /**
   * Creates a list of Hosts with a space shared VM allocation policy (The hosts are physical machines to which VMs will be allocated for serving the cloudlets).
   */
  def createHost: Host = {

    val peList = (1 to HOST_PES).map(pe => new PeSimple(1000)).toList
    val ram = conf.getInt(SIM + "." + "host" + ".ram")
    val bw = conf.getInt(SIM + "." + "host" + ".bw")
    val storage = conf.getInt(SIM + "." + "host" + ".storage")
    new HostSimple(ram, bw, storage, asJava[Pe](peList)).setVmScheduler(new VmSchedulerSpaceShared())
  }

  /**
   * Creates a list of VMs using the time shared scheduler policy to serve the processing elements of the cloudlets.
   */
  def createVms: util.List[Vm] = {
    val list = (1 to VMS).map(vm => new VmSimple(1000, VM_PES).setCloudletScheduler(new CloudletSchedulerTimeShared)).toList
    list.asJava
  }

  /**
   * Creates a list of Cloudlets with utilization mode dynamically set to 25%.
   */
  def createCloudlets: util.List[CloudletSimple] = {
    // UtilizationModel defining the Cloudlets use only 25% of any resource all the time
    val utilizationModel = new UtilizationModelDynamic(0.25)
    val list = (1 to CLOUDLETS).map(c => new CloudletSimple(CLOUDLET_LENGTH, CLOUDLET_PES, utilizationModel)).toList
    list.asJava
  }

  def main(args: Array[String]): Unit = {
    MySimulation2
  }
}
