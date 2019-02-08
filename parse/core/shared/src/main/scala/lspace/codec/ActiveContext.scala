package lspace.codec

import lspace.NS.types
import lspace.codec.exception.FromJsonException
import lspace.librarian.structure.{ClassType, Property}

import scala.collection.immutable.ListMap

//trait ActiveContext[Json] {
//  def `@prefix`: ListMap[String, String]
//  def `@vocab`: Option[String]
//  def `@language`: Option[String]
//  def `@base`: Option[String]
//  def properties: Map[Property, ActiveProperty[Json]]
case class ActiveContext[Json](
    `@prefix`: ListMap[String, String] = ListMap[String, String](),
    `@vocab`: Option[String] = None,
    `@language`: Option[String] = None,
    `@base`: Option[String] = None,
    properties: Map[Property, ActiveProperty[Json]] = Map[Property, ActiveProperty[Json]]()) {

//  def copy(`@prefix`: ListMap[String, String] = `@prefix`,
//           `@vocab`: Option[String] = `@vocab`,
//           `@language`: Option[String] = `@language`,
//           `@base`: Option[String] = `@base`,
//           properties: Map[Property, ActiveProperty[Json]] = properties)(implicit decoder: Decode[Json]): ActiveContext[Json]

  //TODO: map of expanded prefix map values (values can be compacted with leading prefixes)

  def asJson(implicit encoder: Encoder[Json]): Option[Json] = ActiveContext.toJson(this)

  /**
    *
    * @param key
    * @return the compacted iri and a new context with possible additional prefixes
    */
  def compactIri(key: ClassType[_]): (String, ActiveContext[Json]) = {
    key.label.get(`@language`.getOrElse("en")) match {
      case Some(label) if label.nonEmpty =>
        val uriBase = key.iri.stripSuffix(label)
        if (uriBase.nonEmpty && uriBase != key.iri) {
          `@prefix`.find(_._2 == uriBase).map(_._1 + ":" + label).map(iri => iri -> this).getOrElse {
            val prefix = `@prefix`.size.toString
            s"$prefix:$label" -> this.copy(`@prefix` = `@prefix` + (prefix -> uriBase))
          }
        } else key.iri -> this
      case _ =>
        key.iri -> this
    }
  }

  def compactIri(iri: String): String = {
    val validPrefixes = `@prefix`.filter(pv => iri.startsWith(pv._2))
    if (validPrefixes.nonEmpty) {
      val (term, prefix) = validPrefixes.maxBy(_._2.length)
      val suffix         = iri.stripPrefix(prefix)
      term + ":" + suffix
    } else iri
  }

  /**
    *
    * @param term
    * @return
    */
  def expandIri(term: String): String = {
    val (prefix, suffix) =
      if (term.startsWith("http") || term.take(10).contains("://")) "" -> term
      else
        term.split(":") match {
          case Array(prefix, term) => prefix -> term
          case Array(term)         => ""     -> term
          case _                   => ""     -> term
        }
    val iri =
      if (prefix != "") `@prefix`.get(prefix).map(_ + suffix).getOrElse(suffix)
      else
        term //TODO: search vocabularies for matching terms, requires pre-fetching vocabularies or try assembled iri's (@vocab-iri + term)
    if (iri.startsWith("https")) iri
    else if (iri.startsWith("http")) iri.replaceFirst("http", "https")
    else iri
    //    else context.get(term).flatMap(_.get(types.id)).getOrElse(term)
  }

  def expandKeys(obj: Map[String, Json]): Map[String, Json] = obj.map { case (key, value) => expandIri(key) -> value }

  def expectedType(property: Property) =
    properties
      .get(property)
      .flatMap(_.`@type`.headOption)
      .orElse(property.range.headOption)

//  def jsonToString(json: Json): Option[String]
//  def jsonToArray(json: Json): Option[List[Json]]
//  def jsonToMap(json: Json): Option[Map[String, Json]]

  def extractId(obj: Map[String, Json])(implicit decoder: Decoder[Json]) =
    obj.get(types.`@id`).flatMap(decoder.jsonToString).map(expandIri)

  def extractIds(obj: Map[String, Json])(implicit decoder: Decoder[Json]) =
    obj
      .get(types.`@ids`)
      .flatMap(
        json =>
          decoder
            .jsonToList(json)
            .map(_.flatMap(decoder.jsonToString(_).orElse(throw FromJsonException("unknown key/iri format"))))
            .orElse(decoder.jsonToString(json).map(List(_))))
      .getOrElse(List())
      .map(expandIri)

  def extractLabels(obj: Map[String, Json])(implicit decoder: Decoder[Json]): Map[String, String] =
    obj
      .get(types.`@label`)
      .flatMap(
        json =>
          decoder
            .jsonToMap(json)
            .map(_.map {
              case (key, json) =>
                key -> decoder.jsonToString(json).getOrElse(throw FromJsonException("@label value is not a string"))
            })
            .orElse(decoder.jsonToString(json).map(l => Map("en" -> l))))
      .getOrElse(Map())

  def extractComments(obj: Map[String, Json])(implicit decoder: Decoder[Json]): Map[String, String] =
    obj
      .get(types.`@comment`)
      .flatMap(
        json =>
          decoder
            .jsonToMap(json)
            .map(_.map {
              case (key, json) =>
                key -> decoder.jsonToString(json).getOrElse(throw FromJsonException("@comment value is not a string"))
            })
            .orElse(decoder.jsonToString(json).map(l => Map("en" -> l))))
      .getOrElse(Map())

  def extractContainer(obj: Map[String, Json]): Option[Json] =
    obj.get(types.`@container`)

  def extractValue[T](obj: Map[String, Json])(cb: Json => T): Option[T] =
    obj.get(types.`@value`).map(cb)

  def extractFrom(obj: Map[String, Json]): Option[Json] =
    obj.get(types.`@from`)

  def extractTo(obj: Map[String, Json]): Option[Json] =
    obj.get(types.`@to`)
}

object ActiveContext {

  def toJson[Json](context: ActiveContext[Json])(implicit encoder: Encoder[Json]): Option[Json] = {
    val (newActiveContext, result) = context.properties.foldLeft((context, ListMap[String, Json]())) {
      case ((activeContext, result), (key, activeProperty)) =>
        val (keyIri, newActiveContext) = activeContext.compactIri(key)
        List(
          toJson(activeProperty.`@context`).map(types.`@context` -> _),
          _containers(activeProperty).map(types.`@container`     -> _),
          _types(activeProperty).map(types.`@type`               -> _)
        ).flatten match {
          case kv if kv.nonEmpty =>
            newActiveContext -> (result ++ ListMap(keyIri -> encoder.listmapToJson(ListMap(kv: _*))))
          case kv =>
            newActiveContext -> result
        }
    }
    ListMap(newActiveContext.`@prefix`.map {
      case (prefix, iri) => prefix -> encoder.textToJson(iri)
    }.toList ++ result.toList: _*) match {
      case kv if kv.nonEmpty => Some(encoder.listmapToJson(kv))
      case kv                => None
    }
  }

  implicit class WithIriString(iri: String)(implicit activeContext: ActiveContext[_]) {
    lazy val compact: String = activeContext.compactIri(iri)
  }

  private def _containers[Json](activeProperty: ActiveProperty[Json])(implicit encoder: Encoder[Json]): Option[Json] = {
    implicit val activeContext = activeProperty.`@context`
    activeProperty.`@container` match {
      case Nil             => None
      case List(container) => Some(encoder.textToJson(activeContext.compactIri(container.iri)))
      case containers =>
        Some(encoder.listToJson(activeProperty.`@container`.foldLeft(List[Json]()) {
          case (result, container) => result :+ encoder.textToJson(activeContext.compactIri(container.iri))
        }))
    }
  }

  private def _types[Json](activeProperty: ActiveProperty[Json])(implicit encoder: Encoder[Json]): Option[Json] = {
    implicit val activeContext = activeProperty.`@context`
    activeProperty.`@type` match {
      case Nil       => None
      case List(tpe) => Some(encoder.textToJson(activeContext.compactIri(tpe.iri)))
      case tpes =>
        Some(encoder.listToJson(activeProperty.`@type`.foldLeft(List[Json]()) {
          case (result, tpe) => result :+ encoder.textToJson(activeContext.compactIri(tpe.iri))
        }))
    }
  }
}
