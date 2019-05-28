package rest.routes;

import rest.HTTPMessage;

public abstract class Route {

    private HTTPMessage request;
    private HTTPMessage response;

    public Route(HTTPMessage request, HTTPMessage response) {
        this.request = request;
        this.response = response;
    }

}
