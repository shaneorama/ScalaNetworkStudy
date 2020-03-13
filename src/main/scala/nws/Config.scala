package nws

import java.io.{File, FileReader}

import org.yaml.snakeyaml.Yaml

class Config {
  var controlPort = 9999
  var sendBufferSize = 2048
  var readBufferSize = 2048
  var writerIdleTime = 10
  var readerIdleTime = 10
}

object Config {
  def load(configPath: String = "node.config"): Config = {
    val configFile = new File(configPath)
    if(configFile.exists && configFile.canRead) {
      val reader = new FileReader(configFile)
      val config: Config = new Yaml().load(reader)
      reader.close()
      config
    } else {
      new Config
    }
  }
}
