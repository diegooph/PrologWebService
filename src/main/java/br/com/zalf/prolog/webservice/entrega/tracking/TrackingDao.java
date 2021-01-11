package br.com.zalf.prolog.webservice.entrega.tracking;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;

/**
 * Created by didi on 9/15/16.
 */
public interface TrackingDao {

	boolean insertOrUpdateTracking(String path, Long codUnidade) throws SQLException, IOException, ParseException;
}
