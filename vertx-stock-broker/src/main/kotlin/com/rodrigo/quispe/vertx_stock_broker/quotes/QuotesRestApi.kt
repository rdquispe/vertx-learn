package com.rodrigo.quispe.vertx_stock_broker.quotes

import com.rodrigo.quispe.vertx_stock_broker.assets.Asset
import com.rodrigo.quispe.vertx_stock_broker.assets.AssetsRestApi
import io.netty.handler.codec.http.HttpResponseStatus
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.Router
import org.slf4j.LoggerFactory
import java.math.BigDecimal
import java.util.Optional
import java.util.concurrent.ThreadLocalRandom

class QuotesRestApi {

  companion object {

    private val logger = LoggerFactory.getLogger(QuotesRestApi::class.java)

    fun attach(parent: Router) {
      val cachedQuotes = hashMapOf<String, Quote>()
      AssetsRestApi.ASSETS.forEach { symbol ->
        cachedQuotes[symbol] = initRandomQuote(symbol)
      }
      parent.get("/quotes/:asset").handler { context ->
        val assetParam = context.pathParam("asset")
        logger.info("ASSET_PARAMETER: {}", assetParam)

        val maybeQuote = Optional.ofNullable(cachedQuotes[assetParam])
        if (maybeQuote.isEmpty) {
          context.response()
            .setStatusCode(HttpResponseStatus.NOT_FOUND.code())
            .end(
              JsonObject()
                .put("message", "quote for asset $assetParam not available!")
                .put("path", context.normalizedPath()).toBuffer()
            )
          return@handler
        }
        val response = maybeQuote.get().toJsonObject()
        logger.info("PATH {} RESPONDS_WITH {}", context.normalizedPath(), response.encode())
        context.response().end(response.toBuffer())
      }
    }

    private fun initRandomQuote(assetParam: String): Quote =
      Quote(Asset(assetParam), randomValue(), randomValue(), randomValue(), randomValue())

    private fun randomValue(): BigDecimal = BigDecimal.valueOf(ThreadLocalRandom.current().nextDouble(1.0, 100.0))
  }
}
