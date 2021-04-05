import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.utility.JavaModule;

import java.lang.instrument.Instrumentation;

public class SimpleInstru {
    public static void premain(String argument,
                               Instrumentation instrumentation) {

        System.out.println("Agent started at " + System.currentTimeMillis());

        Advice advice = Advice.to(TimeFooter.class);
        new AgentBuilder.Default()
                .type(ElementMatchers.nameContains("ApplicationBeingTransformed"))
                .transform((DynamicType.Builder<?> builder,
                            TypeDescription type,
                            ClassLoader loader,
                            JavaModule module) -> {
                    return builder.visit(advice.on(ElementMatchers.isMethod()));
                }).installOn(instrumentation);
    }

    public static class TimeFooter {

        @Advice.OnMethodExit(onThrowable = Throwable.class)
        public static void exit(@Advice.Origin String methodName) {
            System.out.println("Now exiting: \n"+methodName);
        }
    }
}
