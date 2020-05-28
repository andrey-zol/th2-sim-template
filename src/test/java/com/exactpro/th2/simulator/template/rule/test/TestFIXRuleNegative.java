/*******************************************************************************
 * Copyright 2020 Exactpro (Exactpro Systems Limited)
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

package com.exactpro.th2.simulator.template.rule.test;

import static com.exactpro.th2.simulator.util.ValueUtils.getValue;

import java.util.Collections;
import java.util.List;

import org.jetbrains.annotations.NotNull;

import com.exactpro.th2.infra.grpc.Message;
import com.exactpro.th2.infra.grpc.Message.Builder;
import com.exactpro.th2.infra.grpc.MessageMetadata;
import com.exactpro.th2.simulator.rule.IRule;
import com.exactpro.th2.simulator.rule.test.AbstractRuleTest;
import com.exactpro.th2.simulator.template.rule.FIXRule;
import com.exactpro.th2.simulator.util.MessageUtils;

public class TestFIXRuleNegative extends AbstractRuleTest {

    @Override
    protected int getCountMessages() {
        return 1000;
    }

    @NotNull
    @Override
    protected Message createMessage(int index, @NotNull Builder builder) {
        return MessageUtils.putField(builder, "ClOrdId", "ord_" + index)
                .setMetadata(MessageMetadata.newBuilder().setMessageType("NewOrderSingle"))
                .build();
    }

    @NotNull
    @Override
    protected List<IRule> createRules() {
        return Collections.singletonList(new FIXRule(Collections.singletonMap("ClOrdId", getValue("ord_0"))));
    }

    @Override
    protected boolean checkResultMessages(int index, List<Message> messages) {
        for (Message message : messages) {
            System.out.println(message);
        }
        return index < 1 || messages.size() == 0;
    }
}