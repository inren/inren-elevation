package de.inren.service.banking;

import java.util.List;

import de.inren.service.Initializable;
import net.bull.javamelody.MonitoredWithSpring;

@MonitoredWithSpring
public interface AnalyserService extends Initializable {

	List<String> tokenizeString(String string);
}
