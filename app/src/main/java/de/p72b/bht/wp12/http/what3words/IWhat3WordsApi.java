package de.p72b.bht.wp12.http.what3words;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface IWhat3WordsApi {

    String ENDPOINT_GRID = "/v2/grid";
    String ENDPOINT_FORWARD = "/v2/forward";
    String ENDPOINT_REVERSE = "/v2/reverse";

    @GET(ENDPOINT_GRID)
    Observable<GridResponse> grid(@Query("format") String format,
                                  @Query("bbox") String bbox);

    @GET(ENDPOINT_REVERSE)
    Observable<ReverseResponse> reverse(@Query("format") String format,
                                        @Query("coords") String coords);
}
