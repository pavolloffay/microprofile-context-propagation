package org.eclipse.microprofile.context.spi;

import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.Tracer;
import io.opentracing.util.GlobalTracer;
import java.util.Map;

/**
 * @author Pavol Loffay
 */
public class TracingContextProvider implements ThreadContextProvider {

  private final Tracer tracer = GlobalTracer.get();

  @Override
  public ThreadContextSnapshot currentContext(Map<String, String> props) {
    Span span = tracer.activeSpan();
    return new ThreadContextSnapshot() {
      @Override
      public ThreadContextController begin() {
        Scope scope = tracer.scopeManager().activate(span);
        return new ThreadContextController() {
          @Override
          public void endContext() throws IllegalStateException {
            scope.close();
          }
        };
      }
    };
  }

  @Override
  public ThreadContextSnapshot clearedContext(Map<String, String> props) {
    return null;
  }

  @Override
  public String getThreadContextType() {
    return TracingContextProvider.class.getName();
  }
}
