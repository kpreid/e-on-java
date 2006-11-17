package net.caprest;

import org.erights.e.elib.serial.Marker;

// Copyright 2002 Combex, Inc. under the terms of the MIT X license
// found at http://www.opensource.org/licenses/mit-license.html ...............

/**
 * A Marker-interface to mark a type as being a query-facet of some service.
 * <p/>
 * A "marker interface" is one that doesn't declare any methods, but which
 * represents some testable property for other types to declare about
 * themselves (eg, {@link java.io.Serializable}). A type claims that it has the
 * property in question by extending or implementing the interface. A client
 * tests whether a given object claims to have this propoerty by doing an
 * instanceof check.
 * <p/>
 * (In E we'd instead use auditors, in which case the auditor representing a
 * property could also ensure that those object that claim to have the property
 * in question actually do -- ie, that the claim isn't empty. In this case, the
 * claim made by implementing QueryFacet isn't formalizable, and therefore
 * cannot be audited for, so the more familiar Java marker interface is
 * adequate.)
 * <p/>
 * An object is a QueryFacet if it may be handed to authorized observers of a
 * service, even if they are not authorized users. This, together with {@link
 * UseFacet} and {@link MaintFacet} are inspired by the similar KeyKOS 3-facet
 * pattern, and the attempt to use it to understand the virtues of REST, and
 * thereby combine these virtues with the virtues of capabilities. In this
 * story, the QueryFacet represents claims similar to the <a
 * href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec9.html" >meaning
 * recommended</a> for HTTP Safe Methods, especially GET.
 * <p/>
 * The operations on a QueryFacet are generally either queries, or in support
 * of queries. For example, a QueryFacet support the Java Listener pattern
 * (more conventionally, the "Observer" pattern), by providing
 * <tt>addFooListener</tt> style messages which arrange for the observer to be
 * called back (to avoid polling). Note that such operations are stateful, but
 * they aren't meaningfully stateful (this is the unformalizable part). An
 * observer shouldn't be able to harm the service, and other clients of a
 * service shouldn't ever need to be aware of what an observer might <i>do</i>
 * with a QueryFacet, only what they might learn.
 * <p/>
 * A QueryFacet likewise represents a claim to its clients that the clients
 * need not be concerned about effects on others caused by invoking its
 * operations. This doesn't mean that a client should believe such claims, but
 * this claim can be used to judge the appropriateness of a particular
 * QueryFacet protocol on POLA grounds. Since a QueryFacet doesn't claim to be
 * taking any meaningful actions on behalf of its client, beyond information
 * gathering and reporting, the arguments to the messages declared by a
 * QueryFacet shouldn't need to provide the QueryFacet with any authority to
 * take meaningful actions. Likewise, the results returned by a QueryFacet
 * should not provide their caller with meaningful authority to do anything, as
 * it would then be less safe to hand a QueryFacet to a prospective observer
 * than one might think.
 * <p/>
 * From a {@link UseFacet} or a {@link MaintFacet} one can get a corresponding
 * QueryFacet. Nevertheless, in order to be able to use the type test to find
 * out what is claimed, the UseFacet and MaintFacet must not be made subtypes
 * of the QueryFacet. Although they could be subtypes in the normal oo and
 * Liskov senses, they are not subtypes in the sense of upwards compatible
 * contracts, since the QueryFacet contract specifies what a QueryFacet must
 * not provide as well as what it must provide. Thisis a very crisp example of
 * the capability-design rule (discussed on the e-lang list) "No subtypes that
 * add authority". (I believe that the original KeyKOS maint/use/query pattern
 * did use subtyping in violation of this rule, but because of the unavoidable
 * cost of an OS-based invocation, this decision was probably justified. In any
 * case, EROS follows in KeyKOS's footsteps in this regard.)
 *
 * @author Mark S. Miller
 */
public interface QueryFacet extends Marker {

}
