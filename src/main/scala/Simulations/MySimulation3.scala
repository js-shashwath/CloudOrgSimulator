package Simulations

import java.util

import com.typesafe.config.{Config, ConfigFactory}
import org.cloudbus.cloudsim.allocationpolicies.{VmAllocationPolicy, VmAllocationPolicyBestFit, VmAllocationPolicyFirstFit, VmAllocationPolicyRoundRobin, VmAllocationPolicySimple}
import org.cloudbus.cloudsim.brokers.DatacenterBrokerSimple
import org.cloudbus.cloudsim.cloudlets.{Cloudlet, CloudletSimple}
import org.cloudbus.cloudsim.core.CloudSim
import org.cloudbus.cloudsim.datacenters.DatacenterSimple
import org.cloudbus.cloudsim.hosts.{Host, HostSimple}
import org.cloudbus.cloudsim.resources.{Pe, PeSimple}
import org.cloudbus.cloudsim.schedulers.cloudlet.{CloudletScheduler, CloudletSchedulerSpaceShared, CloudletSchedulerTimeShared}
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelFull
import org.cloudbus.cloudsim.vms.{Vm, VmSimple}
import org.cloudsimplus.builders.tables.{CloudletsTableBuilder, TextTableColumn}
import org.slf4j.{Logger, LoggerFactory}
// import utils.CSPUserInput

import scala.jdk.CollectionConverters._
import scala.jdk.javaapi.CollectionConverters.asJava

/**
 * An cloud model implementation using CloudSim Plus framework which has a predefined DataCenter configurations to provide SaaS/IaaS/PaaS/FaaS cloud services across
 * 3 different models containing data centers that will be created on-the-go as per the user's selection of choice of service.
 *
 * The following logic is used to differentiate between the different data centers for simulating the services as per the user requested configurations :

    - If the user enters only cloudlet length -> The simulation takes places in DataCenter 1 as this is configured to correspond to Faas.

    - If the user enters cloudlet length, number of cloudlets and also number of cloudlet PEs -> The simulation takes place in DataCenter1 as this is configured
    to correspond to SaaS.

    - If the user enters cloudlet length, number of cloudlets, number of cloudlet PEs, and also cloudlet scheduler type -> The simulation takes place in DataCenter2
    as this is configured to correspond to PaaS.

    - If the user enters cloudlet length, number of cloudlets, number of cloudlet PEs, cloudlet scheduler type and also his/her choice of Operating Systems ->
    The simulation takes place in DataCenter3 as this is configured to correspond to IaaS. (The user can enter his/her choice of VM Allocation Policy, but if NA is selected,
    the default policy used will be Round-Robin allocation policy).
 */

object MySimulation3 {

  println("Please enter number of cloudlets: (0 - if nothing is to be set as the input)")
  val numberOfCloudlets: Int = scala.io.StdIn.readInt()
  println("Please enter cloudlet length: (0 - if nothing is to be set as the input")
  val cloudletLength: Int = scala.io.StdIn.readInt()
  println("Please enter number of cloudlet PEs (Processing Elements): (0 - if nothing is to be set as the input)")
  val cloudletPEs: Int = scala.io.StdIn.readInt()
  println("Please choose your cloudlet scheduler policy from among the following: 1.Time-Shared  2.Space-Shared  3.NA  (Please enter between 1-3):")
  val cloudletSchedulerType: Int = scala.io.StdIn.readInt()
  println("Please enter your choice of operating system from among the following: 1.Debian 2.Ubuntu 3.Windows 4. Linux 5.NA (Please enter between 1-5):")
  val operatingSystem: Int = scala.io.StdIn.readInt()
  println("Please enter your choice of VM allocation policy from among the following: 1.Round-Robin  2.Best Fit 3.First Fit 4.Simple 5.NA (Please enter between 1-5):")
  val vmAllocationPolicy: Int = scala.io.StdIn.readInt()

  val SIM = "my_simulation3";
  val conf: Config = ConfigFactory.load(SIM + ".conf")
  val VMS: Int = conf.getInt(SIM + "." + "numVMs")
  val VM_PES: Int = conf.getInt(SIM + "." + "vm" + ".numberPES")

  val LOG: Logger = LoggerFactory.getLogger(getClass)

  def Start()= {}
  val simulation = new CloudSim
  val broker0 = new DatacenterBrokerSimple(simulation)


  if (cloudletLength != 0 && numberOfCloudlets == 0 && cloudletPEs == 0 && getCloudletSchedulerType(cloudletSchedulerType) == null && getOperatingSystemType(operatingSystem) == "NA") {
    //      In Function as a service the broker has access
    createDatacenter(conf.getInt(SIM + "." + "datacenter0" + ".numberHosts"), conf.getInt(SIM + "." + "host" + ".pes"), getVMAllocationPolicy(vmAllocationPolicy), "datacenter0")
  } else if (cloudletLength != 0 && numberOfCloudlets != 0 && cloudletPEs != 0 && getCloudletSchedulerType(cloudletSchedulerType) == null && getOperatingSystemType(operatingSystem) == "NA") {
    //      In Software as a service the broker has access upto the selection of the number of processing elements of the cloudlet
    createDatacenter(conf.getInt(SIM + "." + "datacenter0" + ".numberHosts"), conf.getInt(SIM + "." + "host" + ".pes"), getVMAllocationPolicy(vmAllocationPolicy), "datacenter0")
  } else if (cloudletLength != 0 && numberOfCloudlets != 0 && cloudletPEs != 0 && getCloudletSchedulerType(cloudletSchedulerType) != null && getOperatingSystemType(operatingSystem) == "NA") {
    //      In Platform as a service the broker has access upto the scheduleer policy of the cloudlet
    createDatacenter(conf.getInt(SIM + "." + "datacenter1" + ".numberHosts"), conf.getInt(SIM + "." + "host" + ".pes"), getVMAllocationPolicy(vmAllocationPolicy), "datacenter1")
  } else if (cloudletLength != 0 && numberOfCloudlets != 0 && cloudletPEs != 0 && getCloudletSchedulerType(cloudletSchedulerType) != null && getOperatingSystemType(operatingSystem) != "NA") {
    //      In Infrastructure as a service the broker has access upto the operating system of the data center
    createDatacenter(conf.getInt(SIM + "." + "datacenter2" + ".numberHosts"), conf.getInt(SIM + "." + "host" + ".pes"), getVMAllocationPolicy(vmAllocationPolicy), "datacenter2")
  }

  val vmList: util.List[Vm] = createVms(getCloudletSchedulerType(cloudletSchedulerType))
  val cloudletList: util.List[CloudletSimple] = createCloudlets

  broker0.submitVmList(vmList)
  broker0.submitCloudletList(cloudletList)
  simulation.start

  val finishedCloudlets: util.List[Cloudlet] = broker0.getCloudletFinishedList
  new CloudletsTableBuilder(finishedCloudlets).addColumn(new TextTableColumn("Actual CPU Time"), (cloudlet: Cloudlet) => cloudlet.getActualCpuTime).addColumn(new TextTableColumn("Total Cost"), (cloudlet: Cloudlet) => cloudlet.getTotalCost).build()

  LOG.info("The Simulation has ended.")

  /**
   * Returns the Operating System string as per user's selection.
   */
  def getOperatingSystemType(operatingSystem: Int): String = {
    operatingSystem match {
      case 1 => "Debian"
      case 2 => "Ubuntu"
      case 3 => "Windows"
      case 4 => "Linux"
      case _ => "NA"
    }
  }

  /**
   * Returns the Cloudlet Scheduler Type object as per user's selection.
   */
  def getCloudletSchedulerType(cloudletSchedulerType: Int): CloudletScheduler = {
    cloudletSchedulerType match {
      case 1 => new CloudletSchedulerTimeShared
      case 2 => new CloudletSchedulerSpaceShared
      case _ => null
    }
  }

  /**
   * Returns the VM Allocation Policy object as per user's selection.
   */
  def getVMAllocationPolicy(vmAllocationPolicy: Int): VmAllocationPolicy = {
    vmAllocationPolicy match {
      case 1 => new VmAllocationPolicyRoundRobin
      case 2 => new VmAllocationPolicyBestFit
      case 3 => new VmAllocationPolicyFirstFit
      case 4 => new VmAllocationPolicySimple
      case _ => null
    }
  }

  /**
   * Creates a Datacenter and its Hosts for the basic simulation to check the various parameters
   */
  def createDatacenter(numberHosts: Int, hostPES: Int, vmAllocationPolicyType: VmAllocationPolicy, selectedDC: String): DatacenterSimple = {
    val hostList_new = (1 to numberHosts).map(host => createHost(hostPES)).toList

    val dc = new DatacenterSimple(simulation, hostList_new.asJava,
      if (vmAllocationPolicyType != null) vmAllocationPolicyType
      else new VmAllocationPolicySimple)
    dc.getCharacteristics
      .setCostPerBw(conf.getInt(SIM + "." + selectedDC + ".costPerBw"))
      .setCostPerMem(conf.getInt(SIM + "." + selectedDC + ".costPerMem"))
      .setCostPerSecond(conf.getInt(SIM + "." + selectedDC + ".cost"))
      .setCostPerStorage(conf.getInt(SIM + "." + selectedDC + ".costPerStorage"))
    dc
  }

  /**
   * Creates a list of Hosts (Physical entities to which VMs will be allocated to be run inside Data centers).
   */
  def createHost(hostPES: Int): Host = {
    val peList = (1 to hostPES).map(pe => new PeSimple(1000)).toList
    val ram = conf.getInt(SIM + "." + "host" + ".ram")
    val bw = conf.getInt(SIM + "." + "host" + ".bw")
    val storage = conf.getInt(SIM + "." + "host" + ".storage")
    new HostSimple(ram, bw, storage, asJava[Pe](peList))
  }

  /**
   * Creates a list of VMs.
   */
  def createVms(cloudletSchedulerType: CloudletScheduler): util.List[Vm] = {
    val list = (1 to VMS).map(vm => new VmSimple(1000, VM_PES)
      .setCloudletScheduler(if (cloudletSchedulerType == null) new CloudletSchedulerTimeShared
      else cloudletSchedulerType)).toList
    list.asJava
  }

  /**
   * Creates a list of Cloudlets.
   */
  def createCloudlets: util.List[CloudletSimple] = {
    // Uses the Full Utilization Model meaning a Cloudlet always utilizes a given allocated resource from its Vm at 100%, all the time.
    val utilizationModel = new UtilizationModelFull
    val list = (1 to numberOfCloudlets).map(c => new CloudletSimple(cloudletLength, cloudletPEs, utilizationModel)).toList
    list.asJava
  }

  def main(args: Array[String]): Unit = {
    MySimulation3
  }
}