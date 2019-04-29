package lspace.librarian.traversal.step

import lspace.librarian.traversal._
import lspace.provider.detached.DetachedGraph
import lspace.structure._
import monix.eval.Task
import shapeless.{HList, HNil}

object Group
    extends StepDef("Group", "A group-step groups traversers.", () => CollectingBarrierStep.ontology :: Nil)
    with StepWrapper[Group[ClassType[Any], HList, ClassType[Any], HList]] {

  def toStep(node: Node): Task[Group[ClassType[Any], HList, ClassType[Any], HList]] =
    for {
      by <- Traversal.toTraversal(
        node
          .out(keys.byTraversal)
          .take(1)
          .head)
      value <- Traversal.toTraversal(
        node
          .out(keys.valueTraversal)
          .take(1)
          .head)
    } yield Group[ClassType[Any], HList, ClassType[Any], HList](by, value)

  object keys extends CollectingBarrierStep.Properties {
    object by
        extends PropertyDef(
          lspace.NS.vocab.Lspace + "librarian/step/Group/by",
          "by",
          "A traversal ..",
          `@range` = () => Traversal.ontology :: Nil
        )
    val byTraversal: TypedProperty[Node] = by.property + Traversal.ontology
    object value
        extends PropertyDef(
          lspace.NS.vocab.Lspace + "librarian/step/Group/value",
          "value",
          "A traversal ..",
          `@range` = () => Traversal.ontology :: Nil
        )
    val valueTraversal: TypedProperty[Node] = value.property + Traversal.ontology
  }
  override lazy val properties: List[Property] = keys.by :: CollectingBarrierStep.properties

  trait Properties extends CollectingBarrierStep.Properties {
    lazy val `ns.l-space.eu/librarian/step/Group/by`: Property                  = keys.by
    lazy val `ns.l-space.eu/librarian/step/Group/by @Traversal`: TypedKey[Node] = keys.byTraversal
  }

  implicit def toNode[ET <: ClassType[_], Segments <: HList, ETv <: ClassType[_], SegmentsV <: HList](
      step: Group[ET, Segments, ETv, SegmentsV]): Task[Node] = {
    for {
      node  <- DetachedGraph.nodes.create(ontology)
      by    <- step.by.toNode
      value <- step.value.toNode
      _     <- node.addOut(keys.byTraversal, by)
      _     <- node.addOut(keys.valueTraversal, value)
    } yield node
  }.memoizeOnSuccess

}

case class Group[+ET <: ClassType[_], Segments <: HList, +ETv <: ClassType[_], SegmentsV <: HList](
    by: Traversal[_ <: ClassType[_], ET, Segments],
    value: Traversal[_ <: ClassType[_], ETv, SegmentsV])
    extends CollectingBarrierStep {

  lazy val toNode: Task[Node]      = this
  override def prettyPrint: String = "group(_." + by.toString + ")"
}