/*******************************************************************************
 * Copyright 2020-2020 Exactpro (Exactpro Systems Limited)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.exactpro.th2.simulator.template.rule

import com.exactpro.th2.common.grpc.Message
import com.exactpro.th2.common.grpc.Value
import com.exactpro.th2.common.message.*
import com.exactpro.th2.common.value.getMessage
import com.exactpro.th2.common.value.getString
import com.exactpro.th2.sim.rule.impl.MessageCompareRule

//import java.util.concurrent.atomic.AtomicInteger
import kotlin.random.Random

class KotlinFIXRuleSecurity(field: Map<String, Value>) : MessageCompareRule() {

    init {
        init("SecurityStatusRequest", field)
    }
    override fun handleTriggered(incomeMessage: Message): MutableList<Message> {
        val instrPrice1 = Random.nextInt(50, 100)
        val instrPrice2 = Random.nextInt(50, 100)
        val instrPrice3 = Random.nextInt(50, 100)
        val instrPrice4 = Random.nextInt(50, 100)
        val instrPrice5 = Random.nextInt(50, 100)
        val result = ArrayList<Message>()
        if (!incomeMessage.containsFields("SecurityID")){
            val reject = message("Reject").addFields(
                    "RefTagID", "48",
                    "RefMsgType", "f",
                    "RefSeqNum", incomeMessage.getField("BeginString")?.getMessage()?.getField("MsgSeqNum"),
                    "Text", "Incorrect instrument",
                    "SessionRejectReason", "99"
            )
            result.add(reject.build())
        }
        else {
            when (incomeMessage.getString("SecurityID")) {
                "INSTR1" -> {
                    val highPx = instrPrice1 + Random.nextInt(0, 10)
                    val lowPx = instrPrice1 - Random.nextInt(0, 10)
                    val lastPx = Random.nextInt(highPx, lowPx)
                    val firstPx = Random.nextInt(highPx, lowPx)
                    val SecurityStatus1 = message("SecurityStatus").addFields(
                            "SecurityID", incomeMessage.getField("SecurityID")!!.getString(),
                            "SecurityIDSource", incomeMessage.getField("SecurityIDSource")!!.getString(),
                            "SecurityStatusReqID", incomeMessage.getField("SecurityStatusReqID")!!.getString(),
                            "Currency", "RUB",
                            "MarketID", "Demo Market",
                            "MarketSegmentID", "NEW",
                            "TradingSessionID", "1",
                            "TradingSessionSubID", "3",
                            "UnsolicitedIndicator", "N",
                            "SecurityTradingStatus", "17",
                            "BuyVolume", "0",
                            "SellVolume", "0",
                            "HighPx", highPx,
                            "LowPx", lowPx,
                            "LastPx", lastPx,
                            "FirstPx", firstPx,
                            "Text", "The simulated SecurityStatus has been sent"
                    )
                    result.add(SecurityStatus1.build())
                }
                "INSTR2" -> {
                    val highPx = instrPrice2 + Random.nextInt(0, 10)
                    val lowPx = instrPrice2 - Random.nextInt(0, 10)
                    val lastPx = Random.nextInt(highPx, lowPx)
                    val firstPx = Random.nextInt(highPx, lowPx)
                    val SecurityStatus1 = message("SecurityStatus").addFields(
                            "SecurityID", incomeMessage.getField("SecurityID")!!.getString(),
                            "SecurityIDSource", incomeMessage.getField("SecurityIDSource")!!.getString(),
                            "SecurityStatusReqID", incomeMessage.getField("SecurityStatusReqID")!!.getString(),
                            "Currency", "USD",
                            "MarketID", "Demo Market",
                            "MarketSegmentID", "NEW",
                            "TradingSessionID", "1",
                            "TradingSessionSubID", "3",
                            "UnsolicitedIndicator", "N",
                            "SecurityTradingStatus", "17",
                            "BuyVolume", "0",
                            "SellVolume", "0",
                            "HighPx", highPx,
                            "LowPx", lowPx,
                            "LastPx", lastPx,
                            "FirstPx", firstPx,
                            "Text", "The simulated SecurityStatus has been sent"
                    )
                    result.add(SecurityStatus1.build())
                }
                "INSTR3" -> {
                    val highPx = instrPrice3 + Random.nextInt(0, 10)
                    val lowPx = instrPrice3 - Random.nextInt(0, 10)
                    val lastPx = Random.nextInt(highPx, lowPx)
                    val firstPx = Random.nextInt(highPx, lowPx)
                    val SecurityStatus1 = message("SecurityStatus").addFields(
                            "SecurityID", incomeMessage.getField("SecurityID")!!.getString(),
                            "SecurityIDSource", incomeMessage.getField("SecurityIDSource")!!.getString(),
                            "SecurityStatusReqID", incomeMessage.getField("SecurityStatusReqID")!!.getString(),
                            "Currency", "EUR",
                            "MarketID", "Demo Market",
                            "MarketSegmentID", "NEW",
                            "TradingSessionID", "1",
                            "TradingSessionSubID", "3",
                            "UnsolicitedIndicator", "N",
                            "SecurityTradingStatus", "17",
                            "BuyVolume", "0",
                            "SellVolume", "0",
                            "HighPx", highPx,
                            "LowPx", lowPx,
                            "LastPx", lastPx,
                            "FirstPx", firstPx,
                            "Text", "The simulated SecurityStatus has been sent"
                    )
                    result.add(SecurityStatus1.build())
                }
                "INSTR4" -> {
                    val highPx = instrPrice4 + Random.nextInt(0, 10)
                    val lowPx = instrPrice4 - Random.nextInt(0, 10)
                    val lastPx = Random.nextInt(highPx, lowPx)
                    val firstPx = Random.nextInt(highPx, lowPx)
                    val SecurityStatus1 = message("SecurityStatus").addFields(
                            "SecurityID", incomeMessage.getField("SecurityID")!!.getString(),
                            "SecurityIDSource", incomeMessage.getField("SecurityIDSource")!!.getString(),
                            "SecurityStatusReqID", incomeMessage.getField("SecurityStatusReqID")!!.getString(),
                            "Currency", "GBP",
                            "MarketID", "Demo Market",
                            "MarketSegmentID", "NEW",
                            "TradingSessionID", "1",
                            "TradingSessionSubID", "3",
                            "UnsolicitedIndicator", "N",
                            "SecurityTradingStatus", "17",
                            "BuyVolume", "0",
                            "SellVolume", "0",
                            "HighPx", highPx,
                            "LowPx", lowPx,
                            "LastPx", lastPx,
                            "FirstPx", firstPx,
                            "Text", "The simulated SecurityStatus has been sent"
                    )
                    result.add(SecurityStatus1.build())
                }
                "INSTR5" -> {
                    val highPx = instrPrice5 + Random.nextInt(0, 10)
                    val lowPx = instrPrice5 - Random.nextInt(0, 10)
                    val lastPx = Random.nextInt(highPx, lowPx)
                    val firstPx = Random.nextInt(highPx, lowPx)
                    val SecurityStatus1 = message("SecurityStatus").addFields(
                            "SecurityID", incomeMessage.getField("SecurityID")!!.getString(),
                            "SecurityIDSource", incomeMessage.getField("SecurityIDSource")!!.getString(),
                            "SecurityStatusReqID", incomeMessage.getField("SecurityStatusReqID")!!.getString(),
                            "Currency", "JPY",
                            "MarketID", "Demo Market",
                            "MarketSegmentID", "NEW",
                            "TradingSessionID", "1",
                            "TradingSessionSubID", "3",
                            "UnsolicitedIndicator", "N",
                            "SecurityTradingStatus", "17",
                            "BuyVolume", "0",
                            "SellVolume", "0",
                            "HighPx", highPx,
                            "LowPx", lowPx,
                            "LastPx", lastPx,
                            "FirstPx", firstPx,
                            "Text", "The simulated SecurityStatus has been sent"
                    )
                    result.add(SecurityStatus1.build())
            }
                else -> {
                    val unknownInstr = message("SecurityStatus").addFields(
                            "SecurityID", incomeMessage.getField("SecurityID")!!.getString(),
                            "SecurityIDSource", incomeMessage.getField("SecurityIDSource")!!.getString(),
                            "SecurityStatusReqID", incomeMessage.getField("SecurityStatusReqID")!!.getString(),
                            "UnsolicitedIndicator", "N",
                            "SecurityTradingStatus", "20",
                            "Text", "Unknown or Invalid instrument"
                    )
                    result.add(unknownInstr.build())
                }
            }
        }
        return result
    }
}
