/*
 * Copyright 2013 National Bank of Belgium
 * 
 * Licensed under the EUPL, Version 1.1 or - as soon they will be approved 
 * by the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * http://ec.europa.eu/idabc/eupl
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and 
 * limitations under the Licence.
 */
package internal.ui;

import demetra.bridge.TsConverter;
import demetra.tsprovider.Ts;
import demetra.tsprovider.TsCollection;
import demetra.ui.TsManager;
import demetra.ui.util.NbComponents;
import ec.nbdemetra.ui.tools.ChartTopComponent;
import ec.nbdemetra.ui.tsproviders.DataSourceProviderBuddySupport;
import ec.tss.tsproviders.DataSet;
import ec.tss.tsproviders.IDataSourceProvider;
import java.beans.BeanInfo;
import java.util.Optional;
import org.openide.util.lookup.ServiceProvider;
import demetra.ui.components.HasChart;
import demetra.ui.components.HasTsCollection.TsUpdateMode;
import demetra.ui.TsActionSpi;
import ec.tss.TsMoniker;

/**
 *
 * @author Philippe Charles
 */
@ServiceProvider(service = TsActionSpi.class)
public final class SimpleChartTsAction implements TsActionSpi {

    @Override
    public String getName() {
        return "SimpleChartTsAction";
    }

    @Override
    public String getDisplayName() {
        return "Simple chart";
    }

    @Override
    public void open(Ts ts) {
        TsManager.getDefault().loadAsync(ts, demetra.tsprovider.TsInformationType.All);

        String name = getName() + ts.getMoniker().toString();
        ChartTopComponent c = NbComponents.findTopComponentByNameAndClass(name, ChartTopComponent.class);
        if (c == null) {
            c = new ChartTopComponent();
            c.setName(name);

            TsMoniker tmp = TsConverter.fromTsMoniker(ts.getMoniker());
            Optional<IDataSourceProvider> provider = TsManager.getDefault().lookup(IDataSourceProvider.class, tmp);
            if (provider.isPresent()) {
                DataSet dataSet = provider.get().toDataSet(tmp);
                if (dataSet != null) {
                    c.setIcon(DataSourceProviderBuddySupport.getDefault().getIcon(tmp, BeanInfo.ICON_COLOR_16x16, false).orElse(null));
                    c.setDisplayName(provider.get().getDisplayNodeName(dataSet));
                }
            } else {
                c.setDisplayName(ts.getName());
            }

            c.getChart().setTsCollection(TsCollection.of(ts));
            c.getChart().setTsUpdateMode(TsUpdateMode.None);
            c.getChart().setLegendVisible(false);
            c.getChart().setTitle(ts.getName());
            c.getChart().setLinesThickness(HasChart.LinesThickness.Thick);
            c.open();
        }
        c.requestActive();
    }
}