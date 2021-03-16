package net.test.interceptor;

import net.test.ArgUtils;
import net.test.ArgumentsCollection;
import net.test.CommonActionArgs;
import net.test.DefaultArguments;

import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.SuperCall;

import java.util.function.*;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

public class TimingInterceptorNano {

  public static String NAME = "elapsed_time_in_nano";

  private static String LOG_THRESHOLD_NANO = "log_threshold_nano";

  private static List<String> KNOWN_ARGS =
      Arrays.asList(
          CommonActionArgs.IS_DATE_LOGGED, CommonActionArgs.USE_LOG4J, LOG_THRESHOLD_NANO);

  private CommonActionArgs commonActionArgs;

  private boolean useLog4j;
  private final long logThresholdNano;

  public TimingInterceptorNano(String actionArgs, DefaultArguments defaults) {
    ArgumentsCollection parsed = ArgUtils.parseOptionalArgs(KNOWN_ARGS, actionArgs);
    this.commonActionArgs = new CommonActionArgs(parsed, defaults);
    this.logThresholdNano = parsed.parseLong(LOG_THRESHOLD_NANO, 0);
  }

  @RuntimeType
  public Object intercept(@Origin Method method, @SuperCall Callable<?> callable) throws Exception {
    long start = System.nanoTime();
    try {
      return callable.call();
    } finally {
      long end = System.nanoTime();
      if (this.logThresholdNano == 0 || end - start >= this.logThresholdNano) {
        commonActionArgs.printMsg(
            "TraceAgent (timing): `" + method + "` took " + (end - start) + " nano");
      }
    }
  }
}
