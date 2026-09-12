package controller;

import model.ClientModel;
import view.ClientView;

public class ClientController {

    private ClientView view;
    private ClientModel model;

    public ClientController(ClientView view, ClientModel model) {
        this.view  = view;
        this.model = model;
        listClients();
        view.getBtnSave().addActionListener(e -> saveClient());
        view.getBtnUpdate().addActionListener(e -> updateClient());
        view.getBtnDelete().addActionListener(e -> deleteClient());
    }

    public void listClients() {
        view.showList(model.getAll());
    }

    public void saveClient() {
        boolean ok = model.create(
            view.getCI(), view.getFirstName(), view.getLastName(),
            view.getAge(), view.getPhone(), view.getAddress(),
            view.getWeight(), view.getHeightM());
        view.showMessage(ok ? "Client saved." : "Error: Could not save client.");
        if (ok) { listClients(); view.clearFields(); }
    }

    public void updateClient() {
        boolean ok = model.update(
            view.getSelectedCI(), view.getFirstName(), view.getLastName(),
            view.getAge(), view.getPhone(), view.getAddress(),
            view.getWeight(), view.getHeightM());
        view.showMessage(ok ? "Client updated." : "Error: Could not update client.");
        if (ok) { listClients(); view.clearFields(); }
    }

    public void deleteClient() {
        boolean ok = model.delete(view.getSelectedCI());
        view.showMessage(ok ? "Client deleted." : "Error: Could not delete client.");
        if (ok) { listClients(); view.clearFields(); }
    }
}
