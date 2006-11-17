package net.caprest;

// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

/**
 * The MaintFacet to a service should only be held by those authorized to break
 * the service in order to be able to maintain the service.
 * <p/>
 * Generally, holders of a MaintFacet on a service will have full authority
 * over the service, and so might be described as the "owners" of the service.
 *
 * @author Mark S. Miller
 */
public interface MaintFacet {

    QueryFacet getQueryFacet();

    UseFacet getUseFacet(Object[] args);
}
