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

import com.exactpro.th2.common.grpc.Direction
import com.exactpro.th2.common.grpc.Message
import com.exactpro.th2.common.grpc.Value
import com.exactpro.th2.common.message.*
import com.exactpro.th2.common.value.getInt
import com.exactpro.th2.common.value.getMessage
import com.exactpro.th2.common.value.getString
import com.exactpro.th2.sim.rule.impl.MessageCompareRule
import java.time.LocalDateTime

import java.util.concurrent.atomic.AtomicInteger

class KotlinFIXRule(field: Map<String, Value>) : MessageCompareRule() {

    companion object{
    private var orderId = AtomicInteger(0)
    private var execId = AtomicInteger(0)
    private var TrdMatchId = AtomicInteger(0)

    private var incomeMsgList = arrayListOf<Message>()
    private var ordIdList = arrayListOf<Int>()
    }

    init {
        init("NewOrderSingle", field)
    }
    override fun handleTriggered(incomeMessage: Message): MutableList<Message> {
        incomeMsgList.add(incomeMessage)
        while (incomeMsgList.size > 3) {
            incomeMsgList.removeAt(0)
        }
        val ordId1 = orderId.incrementAndGet()

        ordIdList.add(ordId1)
        while (ordIdList.size > 3) {
            ordIdList.removeAt(0)
        }
        val result = ArrayList<Message>()
        if (!incomeMessage.containsFields("Side")) {
                            val rej = message("Reject").addFields(
                                    "RefTagID", "453",
                                    "RefMsgType", "D",
                                    "RefSeqNum", incomeMessage.getField("BeginString")?.getMessage()?.getField("MsgSeqNum"),
                                    "Text", "Simulating reject message",
                                    "SessionRejectReason", "1"
                            )
                            result.add(rej.build())
                        }
        else {
            if (incomeMessage.getString("SecurityID") == "INSTR6") {
                val bmrej = message("BusinessMessageReject").addFields(
                        "RefTagID", "48",
                        "RefMsgType", "D",
                        "RefSeqNum", incomeMessage.getField("BeginString")?.getMessage()?.getField("MsgSeqNum"),
                        "Text", "Unknown SecurityID",
                        "BusinessRejectReason", "2",
                        "BusinessRejectRefID", incomeMessage.getField("ClOrdID")!!.getString()
                )
                result.add(bmrej.build())
            }
            else {
                when (incomeMessage.getString("Side")) {
                    "1" -> {
                        val execIdNew = execId.incrementAndGet()
                        val transTime = LocalDateTime.now().toString()
                        val fixNew = message("ExecutionReport")
                                .copyFields(incomeMessage,  // fields from NewOrder
                                        "Side",
                                        "Price",
                                        "CumQty",
                                        "ClOrdID",
                                        "SecurityID",
                                        "SecurityIDSource",
                                        "OrdType",
                                        "OrderQty",
                                        "TradingParty",
                                        "TimeInForce",
                                        "OrderCapacity",
                                        "AccountType"
                                )
                                .addFields(
                                        "TransactTime", transTime,
                                        "OrderID", ordId1,
                                        "ExecID", execIdNew,
                                        "LeavesQty", incomeMessage.getField("OrderQty")!!.getString(),
                                        "Text", "Simulated New Order Buy is placed",
                                        "ExecType", "0",
                                        "OrdStatus", "0",
                                        "CumQty", "0"
                                )
                        result.add(fixNew.build())
                        // DropCopy
                        val dcNew = message("ExecutionReport", Direction.FIRST, "dc-demo-server1")
                                .copyFields(incomeMessage,  // fields from NewOrder
                                        "Side",
                                        "Price",
                                        "CumQty",
                                        "ClOrdID",
                                        "SecurityID",
                                        "SecurityIDSource",
                                        "OrdType",
                                        "OrderQty",
                                        "TradingParty",
                                        "TimeInForce",
                                        "OrderCapacity",
                                        "AccountType"
                                )
                                .addFields(
                                        "TransactTime", transTime,
                                        "OrderID", ordId1,
                                        "ExecID", execId.incrementAndGet(),
                                        "LeavesQty", incomeMessage.getField("OrderQty")!!.getString(),
                                        "Text", "Simulated New Order Buy is placed",
                                        "ExecType", "0",
                                        "OrdStatus", "0",
                                        "CumQty", "0"
                                )
                        result.add(dcNew.build())
                    }
                    "2" -> {
                        // Useful variables for buy-side
                        val cumQty1 = incomeMsgList[1].getField("OrderQty")!!.getInt()!!
                        val cumQty2 = incomeMsgList[0].getField("OrderQty")!!.getInt()!!
                        val leavesQty1 = incomeMessage.getField("OrderQty")!!.getInt()!! - cumQty1
                        val leavesQty2 = incomeMessage.getField("OrderQty")!!.getInt()!! - (cumQty1 + cumQty2)
                        val order1ClOdrID = incomeMsgList[0].getField("ClOrdID")!!.getString()
                        val order1Price = incomeMsgList[0].getField("Price")!!.getString()
                        val order1Qty = incomeMsgList[0].getField("OrderQty")!!.getString()
                        val order2ClOdrID = incomeMsgList[1].getField("ClOrdID")!!.getString()
                        val order2Price = incomeMsgList[1].getField("Price")!!.getString()
                        val order2Qty = incomeMsgList[1].getField("OrderQty")!!.getString()
                        val repeating1 = message().addFields("NoPartyIDs", listOf(
                                message().addFields(
                                        "PartyRole", "76",
                                        "PartyID", "DEMO-CONN1",
                                        "PartyIDSource", "D"
                                ),
                                message().addFields(
                                        "PartyRole", "17",
                                        "PartyID", "DEMOFIRM2",
                                        "PartyIDSource", "D"
                                ),
                                message().addFields(
                                        "PartyRole", "3",
                                        "PartyID", "0",
                                        "PartyIDSource", "P"
                                ),
                                message().addFields(
                                        "PartyRole", "122",
                                        "PartyID", "0",
                                        "PartyIDSource", "P"
                                ),
                                message().addFields(
                                        "PartyRole", "12",
                                        "PartyID", "3",
                                        "PartyIDSource", "P"
                                )
                        )
                        )
                        val tradeMatchID1 = TrdMatchId.incrementAndGet()
                        val tradeMatchID2 = TrdMatchId.incrementAndGet()
                        // Generator ER
                        // ER FF Order2 for Trader1
                        val execReportId1 = execId.incrementAndGet()
                        val transTime1 = LocalDateTime.now().toString()
                        val trader1Order2fix1 = message("ExecutionReport", Direction.FIRST, "fix-demo-server1")
                                .copyFields(incomeMessage,
                                        "SecurityID",
                                        "SecurityIDSource",
                                        "OrdType",
                                        "OrderCapacity",
                                        "AccountType"
                                )
                                .addFields(
                                        "TransactTime", transTime1,
                                        "TradingParty", repeating1,
                                        "TimeInForce", "0",  // Get from message?
                                        "ExecType", "F",
                                        "OrdStatus", "2",
                                        "CumQty", cumQty1,
                                        "OrderQty", order2Qty,
                                        "Price", order2Price,
                                        "LastPx", order2Price,
                                        "Side", "1",
                                        "LeavesQty", "0",
                                        "ClOrdID", order2ClOdrID,
                                        "OrderID", ordIdList[1],
                                        "ExecID", execReportId1,
                                        "TrdMatchID", tradeMatchID1,
                                        "Text", "The simulated order has been fully traded"
                                )
                        result.add(trader1Order2fix1.build())
                        //DropCopy
                        val trader1Order2dc1 = message("ExecutionReport", Direction.FIRST, "dc-demo-server1")
                                .copyFields(incomeMessage,
                                        "SecurityID",
                                        "SecurityIDSource",
                                        "OrdType",
                                        "OrderCapacity",
                                        "AccountType"
                                )
                                .addFields(
                                        "TransactTime", transTime1,
                                        "TradingParty", repeating1,
                                        "TimeInForce", "0",  // Get from message?
                                        "ExecType", "F",
                                        "OrdStatus", "2",
                                        "CumQty", cumQty1,
                                        "OrderQty", order2Qty,
                                        "Price", order2Price,
                                        "LastPx", order2Price,
                                        "Side", "1",
                                        "LeavesQty", "0",
                                        "ClOrdID", order2ClOdrID,
                                        "OrderID", ordIdList[1],
                                        "ExecID", execReportId1,
                                        "TrdMatchID", tradeMatchID1,
                                        "Text", "The simulated order has been fully traded"
                                )
                        result.add(trader1Order2dc1.build())
                        // ER FF Order1 for Trader1
                        val execReportId2 = execId.incrementAndGet()
                        val transTime2 = LocalDateTime.now().toString()
                        val trader1Order1fix1 = message("ExecutionReport", Direction.FIRST, "fix-demo-server1")
                                .copyFields(incomeMessage,
                                        "SecurityID",
                                        "SecurityIDSource",
                                        "OrdType",
                                        "OrderCapacity",
                                        "AccountType"
                                )
                                .addFields(
                                        "TransactTime", transTime2,
                                        "TradingParty", repeating1,
                                        "TimeInForce", "0",  // Get from message?
                                        "ExecType", "F",
                                        "OrdStatus", "2",
                                        "CumQty", cumQty2,
                                        "OrderQty", order1Qty,
                                        "Price", order1Price,
                                        "LastPx", order1Price,
                                        "Side", "1",
                                        "ClOrdID", order1ClOdrID,
                                        "LeavesQty", "0",
                                        "OrderID", ordIdList[0],
                                        "ExecID", execReportId2,
                                        "TrdMatchID", tradeMatchID2,
                                        "Text", "The simulated order has been fully traded"
                                )
                        result.add(trader1Order1fix1.build())
                        //DropCopy
                        val trader1Order1dc1 = message("ExecutionReport", Direction.FIRST, "dc-demo-server1")
                                .copyFields(incomeMessage,
                                        "SecurityID",
                                        "SecurityIDSource",
                                        "OrdType",
                                        "OrderCapacity",
                                        "AccountType"
                                )
                                .addFields(
                                        "TransactTime", transTime2,
                                        "TradingParty", repeating1,
                                        "TimeInForce", "0",  // Get from message?
                                        "ExecType", "F",
                                        "OrdStatus", "2",
                                        "CumQty", cumQty2,
                                        "OrderQty", order1Qty,
                                        "Price", order1Price,
                                        "LastPx", order1Price,
                                        "Side", "1",
                                        "ClOrdID", order1ClOdrID,
                                        "LeavesQty", "0",
                                        "OrderID", ordIdList[0],
                                        "ExecID", execReportId2,
                                        "TrdMatchID", tradeMatchID2,
                                        "Text", "The simulated order has been fully traded"
                                )
                        result.add(trader1Order1dc1.build())
                        // ER1 PF Order3 for Trader2
                        val repeating2 = message().addFields("NoPartyIDs", listOf(
                                message().addFields(
                                        "PartyRole", "76",
                                        "PartyID", "DEMO-CONN2",
                                        "PartyIDSource", "D"
                                ),
                                message().addFields(
                                        "PartyRole", "17",
                                        "PartyID", "DEMOFIRM1",
                                        "PartyIDSource", "D"
                                ),
                                message().addFields(
                                        "PartyRole", "3",
                                        "PartyID", "0",
                                        "PartyIDSource", "P"
                                ),
                                message().addFields(
                                        "PartyRole", "122",
                                        "PartyID", "0",
                                        "PartyIDSource", "P"
                                ),
                                message().addFields(
                                        "PartyRole", "12",
                                        "PartyID", "3",
                                        "PartyIDSource", "P"
                                )
                        )
                        )
                        val execReportId3 = execId.incrementAndGet()
                        val trader2Order3fix1 = message("ExecutionReport", Direction.FIRST, "fix-demo-server2")
                                .copyFields(incomeMessage,
                                        "TimeInForce",
                                        "Side",
                                        "Price",
                                        "ClOrdID",
                                        "SecurityID",
                                        "SecurityIDSource",
                                        "OrdType",
                                        "OrderCapacity",
                                        "AccountType"
                                )
                                .addFields(
                                        "TransactTime", transTime1,
                                        "TradingParty", repeating2,
                                        "ExecType", "F",
                                        "OrdStatus", "1",
                                        "LastPx", order2Price,
                                        "CumQty", cumQty1,
                                        "OrderQty", incomeMessage.getField("OrderQty")!!.getString(),
                                        "LeavesQty", leavesQty1,
                                        "OrderID", ordId1,
                                        "ExecID", execReportId3,
                                        "TrdMatchID", tradeMatchID1,
                                        "Text", "The simulated order has been partially traded"
                                )
                        result.add(trader2Order3fix1.build())
                        //DropCopy
                        val trader2Order3dc1 = message("ExecutionReport", Direction.FIRST, "dc-demo-server2")
                                .copyFields(incomeMessage,
                                        "TimeInForce",
                                        "Side",
                                        "Price",
                                        "ClOrdID",
                                        "SecurityID",
                                        "SecurityIDSource",
                                        "OrdType",
                                        "OrderCapacity",
                                        "AccountType"
                                )
                                .addFields(
                                        "TransactTime", transTime1,
                                        "TradingParty", repeating2,
                                        "ExecType", "F",
                                        "OrdStatus", "1",
                                        "LastPx", order2Price,
                                        "CumQty", cumQty1,
                                        "OrderQty", incomeMessage.getField("OrderQty")!!.getString(),
                                        "LeavesQty", leavesQty1,
                                        "OrderID", ordId1,
                                        "ExecID", execReportId3,
                                        "TrdMatchID", tradeMatchID1,
                                        "Text", "The simulated order has been partially traded"
                                )
                        result.add(trader2Order3dc1.build())
                        // ER2 PF Order3 for Trader2
                        val execReportId4 = execId.incrementAndGet()
                        val trader2Order3fix2 = message("ExecutionReport", Direction.FIRST, "fix-demo-server2")
                                .copyFields(incomeMessage,
                                        "TimeInForce",
                                        "Side",
                                        "Price",
                                        "ClOrdID",
                                        "SecurityID",
                                        "SecurityIDSource",
                                        "OrdType",
                                        "OrderCapacity",
                                        "AccountType"
                                )
                                .addFields(
                                        "TransactTime", transTime2,
                                        "TradingParty", repeating2,
                                        "ExecType", "F",
                                        "OrdStatus", "1",
                                        "LastPx", order1Price,
                                        "CumQty", cumQty1 + cumQty2,
                                        "OrderQty", incomeMessage.getField("OrderQty")!!.getString(),
                                        "LeavesQty", leavesQty2,
                                        "OrderID", ordId1,
                                        "ExecID", execReportId4,
                                        "TrdMatchID", tradeMatchID2,
                                        "Text", "The simulated order has been partially traded"
                                )
                        result.add(trader2Order3fix2.build())
                        //DropCopy
                        val trader2Order3dc2 = message("ExecutionReport", Direction.FIRST, "dc-demo-server2")
                                .copyFields(incomeMessage,
                                        "TimeInForce",
                                        "Side",
                                        "Price",
                                        "ClOrdID",
                                        "SecurityID",
                                        "SecurityIDSource",
                                        "OrdType",
                                        "OrderCapacity",
                                        "AccountType"
                                )
                                .addFields(
                                        "TransactTime", transTime2,
                                        "TradingParty", repeating2,
                                        "ExecType", "F",
                                        "OrdStatus", "1",
                                        "LastPx", order1Price,
                                        "CumQty", cumQty1 + cumQty2,
                                        "OrderQty", incomeMessage.getField("OrderQty")!!.getString(),
                                        "LeavesQty", leavesQty2,
                                        "OrderID", ordId1,
                                        "ExecID", execReportId4,
                                        "TrdMatchID", tradeMatchID2,
                                        "Text", "The simulated order has been partially traded"
                                )
                        result.add(trader2Order3dc2.build())
                        // ER3 CC Order3 for Trader2
                        val execReportId5 = execId.incrementAndGet()
                        val transTime3 = LocalDateTime.now().toString()
                        val trader2Order3fix3 = message("ExecutionReport", Direction.FIRST, "fix-demo-server2")
                                .copyFields(incomeMessage,
                                        "TimeInForce",
                                        "Side",
                                        "Price",
                                        "ClOrdID",
                                        "SecurityID",
                                        "SecurityIDSource",
                                        "OrdType",
                                        "TradingParty",
                                        "OrderCapacity",
                                        "AccountType"
                                )
                                .addFields(
                                        "TransactTime", transTime3,
                                        "ExecType", "C",
                                        "OrdStatus", "C",
                                        "CumQty", cumQty1 + cumQty2,
                                        "LeavesQty", "0",
                                        "OrderQty", incomeMessage.getField("OrderQty")!!.getString(),
                                        "OrderID", ordId1,
                                        "ExecID", execReportId5,
                                        "Text", "The remaining part of simulated order has been expired"
                                )
                        result.add(trader2Order3fix3.build())
                        //DropCopy
                        val trader2Order3dc3 = message("ExecutionReport", Direction.FIRST, "dc-demo-server2")
                                .copyFields(incomeMessage,
                                        "TimeInForce",
                                        "Side",
                                        "Price",
                                        "ClOrdID",
                                        "SecurityID",
                                        "SecurityIDSource",
                                        "OrdType",
                                        "TradingParty",
                                        "OrderCapacity",
                                        "AccountType"
                                )
                                .addFields(
                                        "TransactTime", transTime3,
                                        "ExecType", "C",
                                        "OrdStatus", "C",
                                        "CumQty", cumQty1 + cumQty2,
                                        "LeavesQty", "0",
                                        "OrderQty", incomeMessage.getField("OrderQty")!!.getString(),
                                        "OrderID", ordId1,
                                        "ExecID", execReportId5,
                                        "Text", "The remaining part of simulated order has been expired"
                                )
                        result.add(trader2Order3dc3.build())
                    }
                }
            }
        }
        return result
    }
}




