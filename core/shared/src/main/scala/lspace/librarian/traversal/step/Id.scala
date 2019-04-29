package lspace.librarian.traversal.step

import lspace.librarian.traversal._
import lspace.provider.detached.DetachedGraph
import lspace.structure._
import monix.eval.Task

case object Id
    extends StepDef("Id",
                    "An id-step returns the id from the resource held by the traverser.",
                    () => MoveStep.ontology :: Nil)
    with StepWrapper[Id]
    with Id {

  def toStep(node: Node): Task[Id] = Task.now(this)

  object keys extends MoveStep.Properties
  override lazy val properties: List[Property] = MoveStep.properties
  trait Properties extends MoveStep.Properties

  lazy val toNode: Task[Node] = DetachedGraph.nodes.create(ontology).memoizeOnSuccess

  override def prettyPrint: String = "id"
}

trait Id extends MoveStep