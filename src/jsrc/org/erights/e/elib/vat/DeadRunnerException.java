package org.erights.e.elib.vat;

/*
Copyright University of Southampton IT Innovation Centre, 2010,
under the terms of the MIT X license, available from
http://www.opensource.org/licenses/mit-license.html
*/

/* Indicates that something couldn't be queued because the Vat has a
 * DeadRunner.
 */
public final class DeadRunnerException extends RuntimeException {
    public DeadRunnerException(Throwable cause) {
        super(cause.getMessage(), cause);
    }
}
