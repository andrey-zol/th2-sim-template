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

package com.exactpro.th2.simulator.template.rule.test;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.jetbrains.annotations.NotNull;

import com.exactpro.th2.common.message.MessageUtilsKt;
import com.exactpro.th2.common.value.ValueUtilsKt;
import com.exactpro.th2.infra.grpc.Message;
import com.exactpro.th2.infra.grpc.Message.Builder;
import com.exactpro.th2.infra.grpc.MessageMetadata;
import com.exactpro.th2.infra.grpc.Value;
import com.exactpro.th2.simulator.rule.IRule;
import com.exactpro.th2.simulator.rule.test.AbstractRuleTest;
import com.exactpro.th2.simulator.template.rule.FIXRule;

public class TestFIXRuleNegative extends AbstractRuleTest {

    @Override
    protected int getCountMessages() {
        return 1000;
    }

    @NotNull
    @Override
    protected Message createMessage(int index, @NotNull Builder builder) {
        return MessageUtilsKt.addField(builder, "ClOrdId", "ord_" + index)
                .setMetadata(MessageMetadata.newBuilder().setMessageType("NewOrderSingle"))
                .build();
    }

    @NotNull
    @Override
    protected List<IRule> createRules() {
        var arguments = new HashMap<String, Value>();
        arguments.put("ClOrdId", ValueUtilsKt.toValue("ord_0"));
        return Collections.singletonList(new FIXRule(arguments));
    }

    @Override
    protected boolean checkResultMessages(int index, List<Message> messages) {
        return index < 1 || messages.size() == 0;
    }
}
