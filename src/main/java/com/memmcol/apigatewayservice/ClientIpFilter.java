package com.memmcol.apigatewayservice;

import jakarta.ws.rs.core.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class ClientIpFilter implements GlobalFilter {

    private static final Logger log =
            LoggerFactory.getLogger(ClientIpFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange,
                             GatewayFilterChain chain) {

        String xForwardedFor = exchange.getRequest()
                .getHeaders()
                .getFirst("X-Forwarded-For");

        String clientIp;

        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            clientIp = xForwardedFor.split(",")[0].trim();
        } else {
            clientIp = exchange.getRequest()
                    .getRemoteAddress()
                    .getAddress()
                    .getHostAddress();
        }

        ServerHttpRequest mutatedRequest = exchange.getRequest()
                .mutate()
                .header("X-Client-IP", clientIp)
                .build();

        log.info("Resolved Client IP: {}", clientIp);

        return chain.filter(exchange.mutate()
                .request(mutatedRequest)
                .build());
    }
}

//@Component
//public class ClientIpFilter implements GlobalFilter {
//
//    private static final Logger log =
//            LoggerFactory.getLogger(ClientIpFilter.class);
//
//    @Override
//    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
//
//        var request = exchange.getRequest();
//
//        String forwardedFor = request.getHeaders().getFirst("X-Forwarded-For");
//        String realIp = request.getHeaders().getFirst("X-Real-IP");
//
//        String clientIp;
//
//        if (forwardedFor != null && !forwardedFor.isBlank()) {
//            clientIp = forwardedFor.split(",")[0].trim();
//        } else if (realIp != null) {
//            clientIp = realIp;
//        } else {
//            var remote = request.getRemoteAddress();
//            clientIp = (remote != null && remote.getAddress() != null)
//                    ? remote.getAddress().getHostAddress()
//                    : "unknown";
//        }
//
//        log.info("Client IP resolved: {}", clientIp);
//        log.info("X-Forwarded-For: {}", forwardedFor);
//        log.info("RemoteAddress: {}", request.getRemoteAddress());
//
//        ServerHttpRequest mutatedRequest = request.mutate()
//                .header("X-Client-IP", clientIp)
//                .build();
//
//        return chain.filter(exchange.mutate().request(mutatedRequest).build());
//    }
//}


///-------------------------
//@Component
//public class ClientIpFilter implements GlobalFilter {
//
//    private static final Logger log =
//            LoggerFactory.getLogger(ClientIpFilter.class);
//
//    @Override
//    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
//
//        var request = exchange.getRequest();
//
//        String clientIp = exchange.getRequest().getRemoteAddress() != null
//                ? exchange.getRequest().getRemoteAddress().getAddress().getHostAddress()
//                : "unknown";
//
////        String clientIp =  exchange.getRequest().getRemoteAddress().getAddress().getHostAddress();
//
//        String forwarded = request.getHeaders().getFirst("X-Forwarded-For");
//
//        log.info("Remote IP (TCP): {}", clientIp);
//        log.info("X-Forwarded-For (incoming): {}", forwarded);
//
//        ServerHttpRequest mutatedRequest = exchange.getRequest()
//                .mutate()
//                .headers(h -> {
//                    h.remove("X-Forwarded-For");
//                    h.remove("X-Real-IP");
//                    h.set("X-Client-IP", clientIp);
//                })
//                .build();
//
//        return chain.filter(exchange.mutate().request(mutatedRequest).build());
//    }
//}

///--------------------------

//@GET
//@Produces("application/json,application/xml")
//@Path("{merchantname}/{transactionreference}/")
//public Transaction verifyTransaction(@Context HttpServletRequest headers,
//                                     @HeaderParam("secretKey") String secretKey,
//                                     @PathParam("transactionreference") PathSegment transactionreference,
//                                     @QueryParam("postpaid") boolean customerType,
//                                     @PathParam("merchantname") String merchantname,
//                                     @QueryParam("isDualMode") boolean isDualMode) throws WebAppException {
//    //TODO return proper representation object
//    Transaction trans = new Transaction();
//    Customer cust = new Customer();
//    String transReference = transactionreference.getPath();
//    String referenceType = "transref";
////        String referenceType = transactionreference.getMatrixParameters().getFirst(Customer.CUSTOMER_REFERENCE_TYPE_VALUE);
//    String customerTypeValue = Customer.CUSTOMER_TYPE_PRE_PAID;
//    // System.out.println("headers is "+ headers.getRemoteAddr());
//    try {
//        checkAuthorization(merchantname, headers.getRemoteAddr(),secretKey);
//
//        if (customerType) {
//            customerTypeValue = Customer.CUSTOMER_TYPE_POST_PAID;
//            referenceType = "receiptno";
//        }
//
//        if (referenceType.equalsIgnoreCase(Transaction.TRANSACTION_SEARCH_BY_RECEIPT_NUMBER)) {
//            trans = trans.getPaymentReference(customerTypeValue, transReference, "R", merchantname, isDualMode);
//        } else if (referenceType.equalsIgnoreCase(Transaction.TRANSACTION_SEARCH_BY_TRANS_REF)) {
//
//            trans = trans.getPaymentReference(customerTypeValue, transReference, "T", merchantname, isDualMode);
//
//        } else {
//            throw new WebApplicationException(Response.Status.NOT_FOUND);
//        }
//
//    } catch (WebApplicationException ex) {
//        trans.setResponsecode("99");
//        trans.setResponsedesc(ex.getResponse().getEntity().toString());
//        throw ex;
//    } catch (Exception ex) {
//        ex.printStackTrace();
//        trans.setResponsecode("99");
//        trans.setResponsedesc("An unexpected error occurred: " + ex.getMessage());
////            throw new WebApplicationException(Response.status(Response.Status.INTERNAL_SERVER_ERROR)
////                                                      .entity("An unexpected error occurred")
////                                                      .build());
//    }
//    return trans;
//}