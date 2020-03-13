package nws

class Ports {
  var connections: Seq[NodeDescriptor] = _
}


case class NodeDescriptor(id: String) extends AnyVal