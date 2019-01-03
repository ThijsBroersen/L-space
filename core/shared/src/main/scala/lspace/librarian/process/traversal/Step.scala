package lspace.librarian.process.traversal

import lspace.librarian.process.traversal.step._
import lspace.librarian.structure._
import lspace.librarian.structure.Ontology.OntologyDef

object Step extends OntologyDef(lspace.NS.vocab.Lspace + "librarian/Step", Set(), "Step", "Step") {

  def toStep(node: Node): Step = node match {
    case step: Step => step
    case _ =>
      node.labels match {
        case list if list.contains(step.G.ontology)        => G.wrap(node)
        case list if list.contains(step.N.ontology)        => N.wrap(node)
        case list if list.contains(step.V.ontology)        => V.wrap(node)
        case list if list.contains(step.R.ontology)        => R.wrap(node)
        case list if list.contains(step.E.ontology)        => E.wrap(node)
        case list if list.contains(step.Drop.ontology)     => Drop.wrap(node)
        case list if list.contains(step.Dedup.ontology)    => Dedup.wrap(node)
        case list if list.contains(step.Out.ontology)      => Out.wrap(node)
        case list if list.contains(step.OutMap.ontology)   => OutMap.wrap(node)
        case list if list.contains(step.OutE.ontology)     => OutE.wrap(node)
        case list if list.contains(step.OutV.ontology)     => OutV.wrap(node)
        case list if list.contains(step.OutEMap.ontology)  => OutEMap.wrap(node)
        case list if list.contains(step.Group.ontology)    => Group.wrap(node)
        case list if list.contains(step.Path.ontology)     => Path.wrap(node)
        case list if list.contains(step.Id.ontology)       => Id.wrap(node)
        case list if list.contains(step.In.ontology)       => In.wrap(node)
        case list if list.contains(step.InMap.ontology)    => InMap.wrap(node)
        case list if list.contains(step.InE.ontology)      => InE.wrap(node)
        case list if list.contains(step.InV.ontology)      => InV.wrap(node)
        case list if list.contains(step.InEMap.ontology)   => InEMap.wrap(node)
        case list if list.contains(step.Has.ontology)      => Has.wrap(node)
        case list if list.contains(step.HasNot.ontology)   => HasNot.wrap(node)
        case list if list.contains(step.HasId.ontology)    => HasId.wrap(node)
        case list if list.contains(step.HasIri.ontology)   => HasIri.wrap(node)
        case list if list.contains(step.HasLabel.ontology) => HasLabel.wrap(node)
        case list if list.contains(step.HasValue.ontology) => HasValue.wrap(node)
        case list if list.contains(step.Coin.ontology)     => Coin.wrap(node)
        case list if list.contains(step.As.ontology)       => As.wrap(node)
        case list if list.contains(step.Repeat.ontology)   => Repeat.wrap(node)
        case list if list.contains(step.Select.ontology)   => Select.wrap(node)
        case list if list.contains(step.Project.ontology)  => Project.wrap(node)
        case list if list.contains(step.Where.ontology)    => Where.wrap(node)
        case list if list.contains(step.And.ontology)      => And.wrap(node)
        case list if list.contains(step.Or.ontology)       => Or.wrap(node)
        case list if list.contains(step.Not.ontology)      => Not.wrap(node)
        case list if list.contains(step.Union.ontology)    => Union.wrap(node)
        case list if list.contains(step.Coalesce.ontology) => Coalesce.wrap(node)
        case list if list.contains(step.Local.ontology)    => Local.wrap(node)
        case list if list.contains(step.Range.ontology)    => Range.wrap(node)
        case list if list.contains(step.Label.ontology)    => Label.wrap(node)
        case list if list.contains(step.Limit.ontology)    => Limit.wrap(node)
        case list if list.contains(step.Tail.ontology)     => Tail.wrap(node)
        case list if list.contains(step.Order.ontology)    => Order.wrap(node)
        case list if list.contains(step.Count.ontology)    => Count.wrap(node)
        case list if list.contains(step.Is.ontology)       => Is.wrap(node)
        case list if list.contains(step.Sum.ontology)      => Sum.wrap(node)
        case list if list.contains(step.Max.ontology)      => Max.wrap(node)
        case list if list.contains(step.Min.ontology)      => Min.wrap(node)
        case list if list.contains(step.Mean.ontology)     => Mean.wrap(node)
        case list =>
          throw new Exception(s"No valid Step-ontology found for types ${list}")
      }
  }

  object keys {}

  lazy val steps: List[StepDef] = List(
    G,
    N,
    V,
    R,
    E,
    Drop,
    Dedup,
    Out,
    OutMap,
    OutE,
    OutV,
    OutEMap,
    Group,
    Path,
    Id,
    In,
    InMap,
    InE,
    InV,
    InEMap,
    Has,
    HasNot,
    HasId,
    HasIri,
    HasLabel,
    HasValue,
    Coin,
    As,
    Repeat,
    Select,
    Project,
    Where,
    And,
    Or,
    Not,
    Union,
    Coalesce,
    Local,
    Range,
    Label,
    Limit,
    Tail,
    Order,
    Count,
    Is,
    Sum,
    Max,
    Min,
    Mean
  )
}
trait Step extends Node

trait GraphStep extends Step
object GraphStep extends StepDef(label = "GraphStep", comment = "GraphStep", () => Step.ontology :: Nil) {
  object keys extends Step.Properties
  override lazy val properties: List[Property] = Step.properties
  trait Properties extends Step.Properties
}
trait Terminate extends Step
object Terminate extends StepDef(label = "Terminate", comment = "Terminate", () => Step.ontology :: Nil) {
  object keys extends Step.Properties
  override lazy val properties: List[Property] = Step.properties
  trait Properties extends Step.Properties
}
trait Mutator
trait Aggregator
trait TraverseStep extends Step
object TraverseStep extends StepDef(label = "TraverseStep", comment = "TraverseStep", () => Step.ontology :: Nil) {
  object keys extends Step.Properties
  override lazy val properties: List[Property] = Step.properties
  trait Properties extends Step.Properties
}
trait ResourceStep extends TraverseStep
object ResourceStep
    extends StepDef(label = "ResourceStep", comment = "ResourceStep", () => TraverseStep.ontology :: Nil) {
  object keys extends TraverseStep.Properties
  override lazy val properties: List[Property] = TraverseStep.properties
  trait Properties extends TraverseStep.Properties
}
trait MoveStep extends TraverseStep
object MoveStep extends StepDef(label = "MoveStep", comment = "MoveStep", () => TraverseStep.ontology :: Nil) {

  object keys extends Step.Properties {
    object label
        extends Property.PropertyDef(
          lspace.NS.vocab.Lspace + "librarian/MoveStep/label",
          "label",
          "A label",
          container = List(lspace.NS.types.`@set`),
          `@range` = () => Ontology.ontology :: Property.ontology :: DataType.ontology :: Nil
        ) {}
    lazy val labelUrl: TypedProperty[IriResource] = label.property + DataType.default.`@url`
  }

  override lazy val properties: List[Property] = List(keys.label).map(_.property)

  /**
    * mirror of properties in object keys
    */
  trait Properties extends TraverseStep.Properties {
    lazy val `ns.l-space.eu/librarian/MoveStep/label`: Property                   = keys.label
    lazy val `ns.l-space.eu/librarian/MoveStep/label @Url`: TypedKey[IriResource] = keys.labelUrl
  }
}
trait MapStep extends MoveStep with TraverseStep
object MapStep
    extends StepDef(label = "MapStep", comment = "MapStep", () => MoveStep.ontology :: TraverseStep.ontology :: Nil) {
  object keys extends MoveStep.Properties with TraverseStep.Properties
  override lazy val properties: List[Property] = MoveStep.properties ++ TraverseStep.properties
  trait Properties extends MoveStep.Properties with TraverseStep.Properties
}
trait BarrierStep extends TraverseStep
object BarrierStep extends StepDef(label = "BarrierStep", comment = "BarrierStep", () => TraverseStep.ontology :: Nil) {
  object keys extends TraverseStep.Properties
  override lazy val properties: List[Property] = TraverseStep.properties
  trait Properties extends TraverseStep.Properties
}
trait CollectingStep extends TraverseStep
object CollectingStep
    extends StepDef(label = "CollectingStep", comment = "CollectingStep", () => TraverseStep.ontology :: Nil) {
  object keys extends TraverseStep.Properties
  override lazy val properties: List[Property] = TraverseStep.properties
  trait Properties extends TraverseStep.Properties
}
trait CollectingBarrierStep extends BarrierStep with CollectingStep
object CollectingBarrierStep
    extends StepDef(label = "CollectingBarrierStep",
                    comment = "CollectingBarrierStep",
                    () => BarrierStep.ontology :: CollectingStep.ontology :: Nil) {
  object keys extends BarrierStep.Properties with CollectingStep.Properties
  override lazy val properties: List[Property] = BarrierStep.properties ++ CollectingStep.properties
  trait Properties extends BarrierStep.Properties with CollectingStep.Properties
}
trait ReducingBarrierStep extends BarrierStep
object ReducingBarrierStep
    extends StepDef(label = "ReducingBarrierStep", comment = "ReducingBarrierStep", () => BarrierStep.ontology :: Nil) {
  object keys extends BarrierStep.Properties
  override lazy val properties: List[Property] = BarrierStep.properties
  trait Properties extends BarrierStep.Properties
}
trait SupplyingBarrierStep extends BarrierStep
object SupplyingBarrierStep
    extends StepDef(label = "SupplyingBarrierStep", comment = "SupplyingBarrierStep", () => BarrierStep.ontology :: Nil) {
  object keys extends BarrierStep.Properties
  override lazy val properties: List[Property] = BarrierStep.properties
  trait Properties extends BarrierStep.Properties
}
trait EnvironmentStep extends Step
object EnvironmentStep
    extends StepDef(label = "EnvironmentStep", comment = "EnvironmentStep", () => Step.ontology :: Nil) {
  object keys extends Step.Properties
  override lazy val properties: List[Property] = Step.properties
  trait Properties extends Step.Properties
}

trait FilterStep extends TraverseStep
object FilterStep extends StepDef(label = "FilterStep", comment = "FilterStep", () => TraverseStep.ontology :: Nil) {
  object keys extends TraverseStep.Properties
  override lazy val properties: List[Property] = TraverseStep.properties
  trait Properties extends TraverseStep.Properties
}
trait ClipStep extends FilterStep
object ClipStep extends StepDef(label = "ClipStep", comment = "ClipStep", () => FilterStep.ontology :: Nil) {
  object keys extends FilterStep.Properties
  override lazy val properties: List[Property] = FilterStep.properties
  trait Properties extends FilterStep.Properties
}
trait HasStep extends FilterStep
object HasStep extends StepDef(label = "HasStep", comment = "HasStep", () => FilterStep.ontology :: Nil) {
  sealed trait PropertyLabel[T]
  implicit object IsProperty extends PropertyLabel[Property]
  implicit object IsString   extends PropertyLabel[String]
  sealed trait DataTypeLabel[T]
  implicit object IsDataType extends DataTypeLabel[DataType[_]]
  implicit object IsString1  extends DataTypeLabel[String]

  object keys extends FilterStep.Properties
  override lazy val properties: List[Property] = FilterStep.properties
  trait Properties extends FilterStep.Properties
}

trait ModulateStep extends Step
object ModulateStep extends StepDef(label = "ModulateStep", comment = "ModulateStep", () => Step.ontology :: Nil) {
  object keys extends Step.Properties
  override lazy val properties: List[Property] = FilterStep.properties
  trait Properties extends FilterStep.Properties
}
trait BranchStep extends TraverseStep
object BranchStep extends StepDef(label = "BranchStep", comment = "BranchStep", () => TraverseStep.ontology :: Nil) {
  object keys extends TraverseStep.Properties
  override lazy val properties: List[Property] = TraverseStep.properties
  trait Properties extends TraverseStep.Properties
}

abstract class StepDef(label: String,
                       comment: String = "",
                       `@extends`: () => List[Ontology] = () => List(Step.ontology))
    extends OntologyDef(lspace.NS.vocab.Lspace + s"librarian/step/${label}", Set(), label, comment, `@extends`)

trait StepWrapper[T <: Step] {
  def wrap(node: Node): T
}