package com.rodrigo.quispe.vertx_stock_broker.quotes

import com.rodrigo.quispe.vertx_stock_broker.assets.Asset
import io.vertx.ext.web.Router
import org.slf4j.LoggerFactory
import java.math.BigDecimal
import java.util.concurrent.ThreadLocalRandom

class QuotesRestApi {

  companion object {

    private val logger = LoggerFactory.getLogger(QuotesRestApi::class.java)

    fun attach(parent: Router) {
      parent.get("/quotes/:asset")
        .handler { context ->
          val assetParam = context.pathParam("asset")
          logger.info("ASSET_PARAMETER: {}", assetParam)
          val quote = initRandomQuote(assetParam)
          val response = quote.toJsonObject()
          logger.info("PATH {} RESPONDS_WITH {}", context.normalizedPath(), response.encode())
          context.response().end(response.toBuffer())
        }
    }

    private fun initRandomQuote(assetParam: String): Quote =
      Quote(Asset(assetParam), randomValue(), randomValue(), randomValue(), randomValue())

    private fun randomValue(): BigDecimal =
      BigDecimal.valueOf(ThreadLocalRandom.current().nextDouble(1.0, 100.0))
  }
}
