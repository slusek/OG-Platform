/**
 * Copyright (C) 2009 - 2010 by OpenGamma Inc.
 *
 * Please see distribution for license.
 */
package com.opengamma.engine.function.resolver;

import java.util.List;

import com.opengamma.engine.depgraph.DependencyNode;
import com.opengamma.engine.depgraph.UnsatisfiableDependencyGraphException;
import com.opengamma.engine.function.ParameterizedFunction;
import com.opengamma.engine.value.ValueRequirement;
import com.opengamma.engine.value.ValueSpecification;
import com.opengamma.util.PublicAPI;
import com.opengamma.util.tuple.Pair;

/**
 * Returned by a {@link FunctionResolver} to do the actual resolution.
 */
@PublicAPI
public interface CompiledFunctionResolver {
  
  /**
   * Returns one or more functions capable of satisfying the requirement. If multiple functions can satisfy, they
   * should be returned in descending priority.
   * 
   * @param requirement Output requirement to satisfy
   * @param atNode The node in a dependency graph the function would be assigned to
   * @return the function(s) found
   * @throws UnsatisfiableDependencyGraphException if there is a problem
   */
  List<Pair<ParameterizedFunction, ValueSpecification>> resolveFunction(ValueRequirement requirement, DependencyNode atNode);
  
}
