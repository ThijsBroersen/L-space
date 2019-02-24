package lspace.structure.util

import java.time.{Instant, LocalDate, LocalDateTime, LocalTime}

import lspace.datatype._
import lspace.structure._
import lspace.types.vector.{Geometry, Point, Polygon}

import scala.annotation.implicitNotFound
import scala.collection.immutable.ListSet

@implicitNotFound("Cannot find a ClassType for this type")
trait ClassTypeable[T] extends Serializable {

  type C
  type CT <: ClassType[_]
  def ct: CT
}

object ClassTypeable {

//  def apply[T](implicit clsTpabl: ClassTypeable[T]): Aux[T, clsTpabl.CT] = clsTpabl

  type Aux[T, C0, CT0 <: ClassType[_]] = ClassTypeable[T] { type C = C0; type CT = CT0 }

//  implicit def toCT[T, CT <: ClassType[_]](clsTypeable: ClassTypeable.Aux[T, CT]): CT = clsTypeable.ct

  implicit val default: ClassTypeable.Aux[Any, Any, ClassType[Any]] = new ClassTypeable[Any] {
    type C  = Any
    type CT = ClassType[Any]
    def ct: CT = ClassType.stubAny
  }
  implicit val defaultCA: ClassTypeable.Aux[ClassType[Any], Any, ClassType[Any]] = new ClassTypeable[ClassType[Any]] {
    type C  = Any
    type CT = ClassType[Any]
    def ct: CT = ClassType.stubAny
  }

  implicit val defaultInt: ClassTypeable.Aux[Int, Int, IntType[Int]] = new ClassTypeable[Int] {
    type C  = Int
    type CT = IntType[Int]
    def ct: CT = IntType.datatype
  }

  implicit val defaultDouble: ClassTypeable.Aux[Double, Double, DoubleType[Double]] = new ClassTypeable[Double] {
    type C  = Double
    type CT = DoubleType[Double]
    def ct: CT = DoubleType.datatype
  }

  implicit val defaultLong: ClassTypeable.Aux[Long, Long, LongType[Long]] = new ClassTypeable[Long] {
    type C  = Long
    type CT = LongType[Long]
    def ct: CT = LongType.datatype
  }

  implicit val defaultInstant: ClassTypeable.Aux[Instant, Instant, DateTimeType[Instant]] = new ClassTypeable[Instant] {
    type C  = Instant
    type CT = DateTimeType[Instant]
    def ct: CT = DateTimeType.datatype
  }

  implicit val defaultLocalDateTime: ClassTypeable.Aux[LocalDateTime, LocalDateTime, DateTimeType[LocalDateTime]] =
    new ClassTypeable[LocalDateTime] {
      type C  = LocalDateTime
      type CT = DateTimeType[LocalDateTime]
      def ct: CT = LocalDateTimeType.datatype
    }

  implicit val defaultLocalDate: ClassTypeable.Aux[LocalDate, LocalDate, LocalDateType[LocalDate]] =
    new ClassTypeable[LocalDate] {
      type C  = LocalDate
      type CT = LocalDateType[LocalDate]
      def ct: CT = LocalDateType.datatype
    }

  implicit val defaultLocalTime: ClassTypeable.Aux[LocalTime, LocalTime, LocalTimeType[LocalTime]] =
    new ClassTypeable[LocalTime] {
      type C  = LocalTime
      type CT = LocalTimeType[LocalTime]
      def ct: CT = LocalTimeType.datatype
    }

  implicit val defaultGeometry: ClassTypeable.Aux[Geometry, Geometry, GeometricType[Geometry]] =
    new ClassTypeable[Geometry] {
      type C  = Geometry
      type CT = GeometricType[Geometry]
      def ct: CT = GeometricType.datatype
    }

  implicit val defaultGeopoint: ClassTypeable.Aux[Point, Point, GeometricType[Point]] = new ClassTypeable[Point] {
    type C  = Point
    type CT = GeometricType[Point]
    def ct: CT = GeopointType.datatype
  }

  implicit val defaultGeopolygon: ClassTypeable.Aux[Polygon, Polygon, GeoPolygonType[Polygon]] =
    new ClassTypeable[Polygon] {
      type C  = Polygon
      type CT = GeoPolygonType[Polygon]
      def ct: CT = GeoPolygonType.datatype
    }

  implicit val defaultBoolean: ClassTypeable.Aux[Boolean, Boolean, BoolType[Boolean]] = new ClassTypeable[Boolean] {
    type C  = Boolean
    type CT = BoolType[Boolean]
    def ct: CT = BoolType.datatype
  }

  implicit val defaultString: ClassTypeable.Aux[String, String, TextType[String]] = new ClassTypeable[String] {
    type C  = String
    type CT = TextType[String]
    def ct: CT = TextType.datatype
  }

  implicit def defaultList[T, TOut, CTOut <: ClassType[_]](
      implicit clsTpbl: ClassTypeable.Aux[T, TOut, CTOut]): ClassTypeable.Aux[List[T], List[TOut], ListType[TOut]] =
    new ClassTypeable[List[T]] {
      type C  = List[TOut]
      type CT = ListType[TOut]
      def ct: CT = ListType[TOut](List(clsTpbl.ct.asInstanceOf[ClassType[TOut]])).asInstanceOf[ListType[TOut]]
    }

  implicit def defaultSet[T, TOut, CTOut <: ClassType[_]](
      implicit clsTpbl: ClassTypeable.Aux[T, TOut, CTOut]): ClassTypeable.Aux[Set[T], List[TOut], SetType[TOut]] =
    new ClassTypeable[Set[T]] {
      type C  = List[TOut]
      type CT = SetType[TOut]
      def ct: CT = SetType[TOut](List(clsTpbl.ct.asInstanceOf[ClassType[TOut]])).asInstanceOf[SetType[TOut]]
    }

  implicit def defaultVector[T, TOut, CTOut <: ClassType[_]](implicit clsTpbl: ClassTypeable.Aux[T, TOut, CTOut])
    : ClassTypeable.Aux[Vector[T], Vector[TOut], VectorType[TOut]] =
    new ClassTypeable[Vector[T]] {
      type C  = Vector[TOut]
      type CT = VectorType[TOut]
      def ct: CT = VectorType[TOut](List(clsTpbl.ct.asInstanceOf[ClassType[TOut]])).asInstanceOf[VectorType[TOut]]
    }

  implicit def defaultListSet[T, TOut, CTOut <: ClassType[_]](implicit clsTpbl: ClassTypeable.Aux[T, TOut, CTOut])
    : ClassTypeable.Aux[ListSet[T], ListSet[TOut], ListSetType[TOut]] =
    new ClassTypeable[ListSet[T]] {
      type C  = ListSet[TOut]
      type CT = ListSetType[TOut]
      def ct: CT = ListSetType[TOut](List(clsTpbl.ct.asInstanceOf[ClassType[TOut]])).asInstanceOf[ListSetType[TOut]]
    }

  implicit def defaultMap[
      K,
      V,
      KOut,
      KTOut[+Z] <: ClassType[Z],
      VOut,
      VTOut[+Z] <: ClassType[Z]
  ](implicit clsTpblK: ClassTypeable.Aux[K, KOut, KTOut[KOut]], clsTpblV: ClassTypeable.Aux[V, VOut, VTOut[VOut]])
    : ClassTypeable.Aux[Map[K, V], Map[KOut, VOut], MapType[KOut, VOut]] =
    new ClassTypeable[Map[K, V]] {
      type C  = Map[KOut, VOut]
      type CT = MapType[KOut, VOut]
      def ct: CT =
        MapType[KOut, VOut](List(clsTpblK.ct.asInstanceOf[ClassType[KOut]]),
                            List(clsTpblV.ct.asInstanceOf[ClassType[VOut]]))
    }

  implicit def defaultTuple2[
      A,
      B,
      AOut,
      ATOut <: ClassType[_],
      BOut,
      BTOut <: ClassType[_]
  ](implicit clsTpblA: ClassTypeable.Aux[A, AOut, ATOut],
    clsTpblB: ClassTypeable.Aux[B, BOut, BTOut]): ClassTypeable.Aux[(A, B), (AOut, BOut), Tuple2Type[AOut, BOut]] =
    new ClassTypeable[(A, B)] {
      type C  = (AOut, BOut)
      type CT = Tuple2Type[AOut, BOut]
      def ct: CT = Tuple2Type(List(clsTpblA.ct), List(clsTpblB.ct)).asInstanceOf[Tuple2Type[AOut, BOut]]
    }

  implicit def defaultTuple3[A,
                             Aout,
                             ATout <: ClassType[_],
                             B,
                             Bout,
                             BTout <: ClassType[_],
                             C,
                             Cout,
                             CTout <: ClassType[_]](implicit clsTpblA: ClassTypeable.Aux[A, Aout, ATout],
                                                    clsTpblB: ClassTypeable.Aux[B, Bout, BTout],
                                                    clsTpblC: ClassTypeable.Aux[C, Cout, CTout])
    : ClassTypeable.Aux[(A, B, C), (Aout, Bout, Cout), Tuple3Type[Aout, Bout, Cout]] =
    new ClassTypeable[(A, B, C)] {
      type C  = (Aout, Bout, Cout)
      type CT = Tuple3Type[Aout, Bout, Cout]
      def ct: CT =
        Tuple3Type(List(clsTpblA.ct), List(clsTpblB.ct), List(clsTpblC.ct)).asInstanceOf[Tuple3Type[Aout, Bout, Cout]]
    }

  implicit def defaultTuple4[A,
                             Aout,
                             ATout <: ClassType[_],
                             B,
                             Bout,
                             BTout <: ClassType[_],
                             C,
                             Cout,
                             CTout <: ClassType[_],
                             D,
                             Dout,
                             DTout <: ClassType[_]](implicit clsTpblA: ClassTypeable.Aux[A, Aout, ATout],
                                                    clsTpblB: ClassTypeable.Aux[B, Bout, BTout],
                                                    clsTpblC: ClassTypeable.Aux[C, Cout, CTout],
                                                    clsTpblD: ClassTypeable.Aux[D, Dout, DTout])
    : ClassTypeable.Aux[(A, B, C, D), (Aout, Bout, Cout, Dout), Tuple4Type[Aout, Bout, Cout, Dout]] =
    new ClassTypeable[(A, B, C, D)] {
      type C  = (Aout, Bout, Cout, Dout)
      type CT = Tuple4Type[Aout, Bout, Cout, Dout]
      def ct: CT =
        Tuple4Type(List(clsTpblA.ct), List(clsTpblB.ct), List(clsTpblC.ct), List(clsTpblD.ct))
          .asInstanceOf[Tuple4Type[Aout, Bout, Cout, Dout]]
    }

//  implicit def clsdt[T, CT[+Z] <: ClassType[Z]](implicit ev: ClassTypeable[T]): ClassTypeable[CT[T]] = DataType.urlType[CT[T]]
}
