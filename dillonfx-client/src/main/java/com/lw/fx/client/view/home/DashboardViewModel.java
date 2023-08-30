package com.lw.fx.client.view.home;

import com.lw.fx.client.domain.ActiveProjects;
import com.lw.fx.client.domain.Roects;
import de.saxsys.mvvmfx.ViewModel;
import de.saxsys.mvvmfx.utils.commands.Action;
import de.saxsys.mvvmfx.utils.commands.Command;
import de.saxsys.mvvmfx.utils.commands.DelegateCommand;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.Random;

public class DashboardViewModel implements ViewModel {

    private ObservableList<Roects> roects = FXCollections.observableArrayList();
    private ObservableList<ActiveProjects> activeProjects = FXCollections.observableArrayList();

    private Command initCommand;

    public void initialize() {

        initCommand = new DelegateCommand(() -> new Action() {
            @Override
            protected void action() throws Exception {

                Random random = new Random();
                for (int i = 1; i < 11; i++) {
                    Roects roectsTmp = new Roects();
                    roectsTmp.setProjectName("App design and development");
                    roectsTmp.setNo(i + "");
                    roectsTmp.setStartDate("2023-04-09");
                    roectsTmp.setDueDate("2023-09-09");
                    roectsTmp.setStatus("Work in Progress");
                    roectsTmp.setClients("Halette Boivin");
                    roects.add(roectsTmp);

                    ActiveProjects activeProjectsTmp = new ActiveProjects();
                    activeProjectsTmp.setProjectName("Brand Logo Design");
                    activeProjectsTmp.setProjectLead("Bessie Cooper");
                    activeProjectsTmp.setProgress(random.nextDouble(1));
                    activeProjectsTmp.setAssignee("");
                    activeProjectsTmp.setStatus("status");
                    activeProjectsTmp.setDueDate("2023-09-09");
                    activeProjects.add(activeProjectsTmp);

                }
            }
        }, true); //Async
        initCommand.execute();
    }

    public ObservableList<Roects> getRoects() {
        return roects;
    }

    public ObservableList<ActiveProjects> getActiveProjects() {
        return activeProjects;
    }


}
