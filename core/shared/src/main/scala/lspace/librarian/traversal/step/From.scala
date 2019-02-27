package lspace.librarian.traversal.step

import lspace.librarian.traversal._
import lspace.provider.detached.DetachedGraph
import lspace.structure._

case object From
    extends StepDef("From", "A from-step moves to the origin of the edge", () => MoveStep.ontology :: Nil)
    with StepWrapper[From]
    with From {

  def toStep(node: Node): From = this

  object keys extends MoveStep.Properties
  override lazy val properties: List[Property] = MoveStep.properties
  trait Properties extends MoveStep.Properties

  lazy val toNode: Node            = DetachedGraph.nodes.create(ontology)
  override def prettyPrint: String = "from"
}

trait From extends MoveStep
