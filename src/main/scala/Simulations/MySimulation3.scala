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
 * An example using CloudSim Plus framework which provides the user a choice of
 * configuration between the SaaS/IaaS/PaaS/FaaS cloud provider models containing
 * 3 different data centers that will be created on-the-go as per the user's selection
 * of choice of service.
 *
 * This represents the fifth and final step in the CS 441 Homework 1 issued in Fall 2021 at
 * University of Illinois at Chicago (UIC)
 *
 */

object MySimulation3 {

  println("Welcome to Cloud Plus Simulation Arena ! Please answer a brief questionnaire and we'll get your cloud simulations up and running.")

  println("Please enter number of cloudlets: (0 - if you don't wish to enter any)")
  val numberOfCloudlets: Int = scala.io.StdIn.readInt()
  println("Please enter cloudlet length: (0 - if you don't wish to enter")
  val cloudletLength: Int = scala.io.StdIn.readInt()
  println("Please enter number of cloudlet PEs (Processing Elements): (0 - if you don't wish to enter)")
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
    //      Faas
    createDatacenter(conf.getInt(SIM + "." + "datacenter0" + ".numberHosts"), conf.getInt(SIM + "." + "host" + ".pes")
      , getVMAllocationPolicy(vmAllocationPolicy), "datacenter0")
  } else if (cloudletLength != 0 && numberOfCloudlets != 0 && cloudletPEs != 0 && getCloudletSchedulerType(cloudletSchedulerType) == null && getOperatingSystemType(operatingSystem) == "NA") {
    //      Saas
    createDatacenter(conf.getInt(SIM + "." + "datacenter0" + ".numberHosts"), conf.getInt(SIM + "." + "host" + ".pes")
      , getVMAllocationPolicy(vmAllocationPolicy), "datacenter0")
  } else if (cloudletLength != 0 && numberOfCloudlets != 0 && cloudletPEs != 0 && getCloudletSchedulerType(cloudletSchedulerType) != null && getOperatingSystemType(operatingSystem) == "NA") {
    //      Paas
    createDatacenter(conf.getInt(SIM + "." + "datacenter1" + ".numberHosts"), conf.getInt(SIM + "." + "host" + ".pes")
      , getVMAllocationPolicy(vmAllocationPolicy), "datacenter1")
  } else if (cloudletLength != 0 && numberOfCloudlets != 0 && cloudletPEs != 0 && getCloudletSchedulerType(cloudletSchedulerType) != null && getOperatingSystemType(operatingSystem) != "NA") {
    //      Iaas
    createDatacenter(conf.getInt(SIM + "." + "datacenter2" + ".numberHosts"), conf.getInt(SIM + "." + "host" + ".pes")
      , getVMAllocationPolicy(vmAllocationPolicy), "datacenter2")
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

