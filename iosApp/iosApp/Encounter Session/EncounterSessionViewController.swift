//
//  EncounterSessionViewController.swift
//  iosApp
//
//  Created by Andrii Puhach on 07.02.2021.
//

import UIKit
import shared

class EncounterSessionViewController: UIViewController, UITableViewDataSource, UITableViewDelegate {

    //MARK: widgets
    @IBOutlet weak var actionButton: UIButton!
    
    @IBOutlet weak var timerLabel: UILabel!
    
    @IBOutlet weak var participantsTable: UITableView!
    
    @IBOutlet weak var roundLabel: UILabel!
    
    private var tickData: TickData? = nil
    
    //MARK: params
    var code: String = ""
    
    lazy var viewModel = EncounterSessionViewModel(
        code: self.code
    )
    
    //MARK: lifecycle
    override func viewWillAppear(_ animated: Bool) {
        self.navigationController?.setNavigationBarHidden(false, animated: true)
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        participantsTable.allowsSelection = false
        
        viewModel.data.watch { data in
            guard let data = data else { return }
            
            self.updateRoundLabel(data)
            self.updateTrackerTime(data)
            self.updateTrackerButton(data)
            self.updateNavBarActions(data)
            self.tickData = data
            
            self.participantsTable.reloadData()
        }
        
        self.setupBackButton()
        self.setupParticipantsTable()
        
        title = "Encounter #\(code)"
    }
  
    override func viewWillDisappear(_ animated: Bool) {
        super.viewWillDisappear(animated)
        viewModel.onCleared()
    }
    
    //MARK: table
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return tickData?.participants.count ?? 0
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "participant_cell", for: indexPath) as! ParticipantCell
    
        let participantIndex = indexPath.row
        
        guard let participant = tickData?.participants[participantIndex] else {
            return cell
        }
    
        let isSelected = tickData?.turnIndex ?? -1 == participantIndex
        
        cell.configureWith(
            participant: participant,
            allowDelete: false,
            isSelected: isSelected
        )
     
    
        return cell
    }
    
    //MARK: actions
    
    @IBAction func onActionTouch(_ sender: UIButton) {
        viewModel.doTrackerAction()
    }
    
    @objc func onSkipTouch(_ sender: UIBarButtonItem){
        viewModel.skipTurn()
    }
    
    @objc func onShareTouch(_ sender: UIBarButtonItem){
        let items = [code]
        let ac = UIActivityViewController(activityItems: items, applicationActivities: nil)
        present(ac, animated: true)
    }
    
    @objc func onBackButtonTouch (_ sender: UIBarButtonItem){
        let controller = UIAlertController(
            title: "Already going?",
            message: "If you're last player in the room, game session will be lost.",
            preferredStyle: UIAlertController.Style.alert
        )
        
        let confirmAction = UIAlertAction(title: "Ok", style: .default, handler: { (action: UIAlertAction!) in
            self.navigationController?.popToRootViewController(animated: true)
        })
        
        let cancelAction = UIAlertAction(title: "Cancel", style: .default, handler: { (action: UIAlertAction!) in
        })
        
        controller.addAction(confirmAction)
        controller.addAction(cancelAction)

        present(controller, animated: true, completion: nil)
    }
    
    private func setupBackButton(){
        self.navigationController?.interactivePopGestureRecognizer?.isEnabled = false
        self.navigationItem.setHidesBackButton(true, animated: false)
        
        let backButton = UIBarButtonItem(
            title: "Cancel",
            style: UIBarButtonItem.Style.plain,
            target: self,
            action: #selector(onBackButtonTouch)
        )
        self.navigationItem.leftBarButtonItem = backButton
    }
    
    private func updateNavBarActions(_ data: TickData){
        let skip = UIBarButtonItem(
            title: "Skip",
            style: .plain,
            target: self,
            action: #selector(onSkipTouch)
        )
        let share = UIBarButtonItem(
            barButtonSystemItem: .action, target: self, action: #selector(onShareTouch)
        )
        
        if(data.isSkipTurnAllowed){
            self.navigationItem.rightBarButtonItems = [skip]
            navigationController?.navigationBar.topItem?.rightBarButtonItems = [skip, share]
        } else{
            navigationController?.navigationBar.topItem?.rightBarButtonItems = [share]
        }
    
        navigationController?.toolbarItems = []
    }

    private func updateTrackerButton(_ data: TickData){
        self.actionButton.isHidden = !data.isPlayPauseAllowed
      
        let imageName = data.isPaused ? "play.fill" : "pause.fill"
        let image = UIImage(systemName: imageName)
        self.actionButton.setImage(image, for: .normal)
    }
    
    private func updateTrackerTime(_ data: TickData){

        if data.isInDangerZone {
            self.timerLabel.textColor = UIColor.red
        }
        else {
            self.timerLabel.textColor = UIColor.black
        }
        
        self.timerLabel.text = data.decoratedTick
    }
    
    private func updateRoundLabel(_ data: TickData){
        self.roundLabel.text = "Round # \(data.roundIndex)"
    }
    
    private func setupParticipantsTable(){
        participantsTable.delegate = self
        participantsTable.dataSource = self
        let nib = UINib(nibName: "ParticipantCell", bundle: nil)
        participantsTable.register(nib, forCellReuseIdentifier: "participant_cell")
    }
}
