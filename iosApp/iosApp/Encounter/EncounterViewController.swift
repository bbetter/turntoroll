//
//  CreateEncounterViewController.swift
//  iosApp
//
//  Created by Andrii Puhach on 10.02.2021.
//

import UIKit
import shared
import MBProgressHUD

class EncounterViewController: UIViewController,UITableViewDataSource, UITableViewDelegate {
    
    private let viewModel = EncounterViewModel()
    
    @IBOutlet weak var nameTextField: UITextField!
    
    @IBOutlet weak var dexterityField: UITextField!
    
    @IBOutlet weak var initiativeTextField: UITextField!
    
    @IBOutlet weak var participantsTable: UITableView!
    
    @IBOutlet weak var actionItem: UIBarButtonItem!
    
    var code: String = ""
    
    override func viewWillAppear(_ animated: Bool) {
        self.navigationController?.setNavigationBarHidden(false, animated: true)
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        setupParticipantsTable()
        setupSubscriptions()
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return Int(viewModel.dataCount())
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "participant_cell", for: indexPath) as! ParticipantCell
        
        let index = indexPath.row
        let participant = viewModel.dataAt(index: Int32(index))
        
        cell.configureWith(participant: participant, allowDelete: true, isSelected: false)
        
        cell.deleteButton.tag = index
        
        cell.onDeleteRowClick = { [weak self] index in
            guard let self = self else { return }
            
            self.viewModel.removeParticiapant(participant: participant)
        }
        return cell
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if(segue.identifier == "encounter_segue"){
            
            if let controller = segue.destination as? EncounterSessionViewController{
                controller.code = code
            }
        }
    }
    @IBAction func onAddTouch(_ sender: UIButton) {
        let name = nameTextField.text ?? ""
        let ini = Int32(initiativeTextField.text ?? "") ?? 0
        let dex = Int32(dexterityField.text ?? "") ?? 0
        

        do {
            try viewModel.addParticipant(name: name, ini: ini, dex: dex)
            
            nameTextField.text = ""
            initiativeTextField.text = ""
            dexterityField.text = ""
        }
        catch let error as NSError{
            showErrorDialog(msg: error.localizedDescription)
        }
       
    }
    
    @IBAction func onActionTouch(_ sender: UIBarButtonItem) {
        viewModel.createEncounter().watch { result in
            if result != nil {
                switch result! {
                case is CreateEncounterResult.Loading:
                    MBProgressHUD.showAdded(to: self.view, animated: true)
                case let error as CreateEncounterResult.Error:
                    MBProgressHUD.hide(for: self.view, animated: true)
                    self.showErrorDialog(msg: error.message)
                case let succRes as CreateEncounterResult.Success:
                    MBProgressHUD.hide(for: self.view, animated: true)
                    self.code = succRes.code
                    self.performSegue(withIdentifier: "encounter_segue", sender: self)
                default:
                    NSLog("%s", "Unknown error ")
                }
            }

        }
    }
    
    @IBAction func onDiceTouch(_ sender: Any) {
        let initiative = viewModel.rollInitiative()
        initiativeTextField.text = "\(initiative)"
    }
    
    private func setupSubscriptions(){
        self.viewModel.data.watch { [weak self] data in
            guard let self = self else { return }
            
            if(data?.count ?? 0 == 0){
                self.participantsTable.setNoDataPlaceholder("No participants yet")
            } else{
                self.participantsTable.removeNoDataPlaceholder()
            }
            self.participantsTable.reloadData()
        }
    }
    
    private func setupParticipantsTable(){
        self.participantsTable.delegate = self
        self.participantsTable.dataSource = self
        
        let nib = UINib(nibName: "ParticipantCell", bundle: nil)
        self.participantsTable.register(nib, forCellReuseIdentifier: "participant_cell")
    }
    
    private func showErrorDialog(msg: String){
        let alert = UIAlertController(title: "Error", message: msg, preferredStyle: .alert)
        alert.addAction(UIAlertAction(title: "Cancel", style: .cancel, handler: nil))
        self.present(alert, animated: true)
    }
}
