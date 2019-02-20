package lspace.librarian.traversal.step

import lspace.librarian.traversal._
import lspace.provider.detached.DetachedGraph
import lspace.structure._
import shapeless.{HList, HNil}

object Local extends StepDef("Local", "A local-step ..", () => BranchStep.ontology :: Nil) with StepWrapper[Local] {

  def toStep(node: Node): Local = Local(
    node
      .out(keys.traversalTraversal)
      .take(1)
      .map(
        Traversal
          .toTraversal(_)
          .asInstanceOf[Traversal[ClassType[Any], ClassType[Any], HList]])
      .head
  )

  object keys extends BranchStep.Properties {
    object traversal
        extends PropertyDef(
          lspace.NS.vocab.Lspace + "librarian/step/Local/traversal",
          "traversal",
          "A traversal ..",
          `@range` = () => Traversal.ontology :: Nil
        )
    val traversalTraversal: TypedProperty[Node] = traversal.property + Traversal.ontology
  }
  override lazy val properties: List[Property] = keys.traversal :: BranchStep.properties
  trait Properties extends BranchStep.Properties {
    val traversal          = keys.traversal
    val traversalTraversal = keys.traversalTraversal
  }

  implicit def toNode(local: Local): Node = {
    val node = DetachedGraph.nodes.create(ontology)
    node.addOut(keys.traversalTraversal, local.traversal.toNode)
    node
  }

}

case class Local(traversal: Traversal[_ <: ClassType[_], _ <: ClassType[_], _ <: HList]) extends BranchStep {

  lazy val toNode: Node            = this
  override def prettyPrint: String = "local(_." + traversal.toString + ")"
}