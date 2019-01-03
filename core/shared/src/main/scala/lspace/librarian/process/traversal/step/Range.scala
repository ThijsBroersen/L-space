package lspace.librarian.process.traversal.step

import lspace.librarian.process.traversal._
import lspace.librarian.provider.detached.DetachedGraph
import lspace.librarian.provider.wrapped.WrappedNode
import lspace.librarian.structure._

object Range extends StepDef("Range", "A range ..", () => ClipStep.ontology :: Nil) with StepWrapper[Range] {

  def wrap(node: Node): Range = node match {
    case node: Range => node
    case _ =>
      Range(node.out(Range.keys.lowInt).take(1).head, node.out(Range.keys.highInt).take(1).head, node)
  }

  object keys extends FilterStep.Properties {
    object low
        extends Property.PropertyDef(
          lspace.NS.vocab.Lspace + "librarian/step/Range/low",
          "low",
          "The lower result-index to start from",
          `@range` = () => DataType.default.`@int` :: Nil
        )
    val lowInt: TypedProperty[Int] = low.property + DataType.default.`@int`

    object high
        extends Property.PropertyDef(
          lspace.NS.vocab.Lspace + "librarian/step/Range/high",
          "high",
          "The higher result-index to start from",
          `@range` = () => DataType.default.`@int` :: Nil
        )
    val highInt: TypedProperty[Int] = high.property + DataType.default.`@int`
  }
  override lazy val properties: List[Property] = keys.low.property :: keys.high.property :: ClipStep.properties
  trait Properties extends FilterStep.Properties {
    val low     = keys.low
    val lowInt  = keys.lowInt
    val high    = keys.high
    val highInt = keys.highInt
  }

  def apply(low: Int, high: Int): Range = {
    val node = DetachedGraph.nodes.create(ontology)

    node.addOut(keys.lowInt, low)
    node.addOut(keys.highInt, high)
    Range(low, high, node)
  }

}

case class Range private (low: Int, high: Int, override val value: Node) extends WrappedNode(value) with ClipStep {
  override def prettyPrint: String = s"range($low, $high)"
}