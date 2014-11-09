package example.sugar

import com.explora.ld.dbpedia.DBPedia
import com.explora.pattern.ExploraHelper._

import scala.concurrent.ExecutionContext.Implicits.global

object BaseQuery extends App with DBPedia {


  val resultF = """select distinct ?Concept where {[] a ?Concept}""" executeAndGet "Concept"

  resultF.map { result =>
    result.foreach(println)

  }

}