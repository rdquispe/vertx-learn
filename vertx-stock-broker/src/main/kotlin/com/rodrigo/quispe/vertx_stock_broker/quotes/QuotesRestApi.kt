package com.rodrigo.quispe.vertx_stock_broker.quotes

import com.rodrigo.quispe.vertx_stock_broker.assets.Asset
import com.rodrigo.quispe.vertx_stock_broker.assets.AssetsRestApi
import io.vertx.ext.web.Router
import io.vertx.sqlclient.Pool
import org.slf4j.LoggerFactory
import java.math.BigDecimal
import java.util.concurrent.ThreadLocalRandom

class QuotesRestApi {

  companion object {

    private val logger = LoggerFactory.getLogger(QuotesRestApi::class.java)

    fun attach(parent: Router, db: Pool) {
      val cachedQuotes = hashMapOf<String, Quote>()
      AssetsRestApi.ASSETS.forEach { symbol ->
        cachedQuotes[symbol] = initRandomQuote(symbol)
      }
      parent.get("/quotes/:asset").handler(GetQuoteHandler(cachedQuotes))
      parent.get("/pg/quotes/:asset").handler(GetQuoteFromDatabaseHandler(db))
    }

    private fun initRandomQuote(assetParam: String): Quote =
      Quote(Asset(assetParam), randomValue(), randomValue(), randomValue(), randomValue())

    private fun randomValue(): BigDecimal = BigDecimal.valueOf(ThreadLocalRandom.current().nextDouble(1.0, 100.0))
  }
}
