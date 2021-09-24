import HelperUtils.{CreateLogger, ObtainConfigReference}
import Simulations.{MySimulation1, MySimulation2, MySimulation3}
import com.typesafe.config.ConfigFactory
import org.slf4j.LoggerFactory

object Simulation:
  val logger = CreateLogger(classOf[Simulation])

  @main def runSimulation =
    logger.info(".........Constructing 3 different cloud model.........")
    MySimulation1.Start()
    MySimulation2.Start()
    MySimulation3.Start()
    logger.info(".........Finished the cloud simulations.........")

class Simulation