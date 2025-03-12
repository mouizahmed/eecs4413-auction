package com.teamAgile.backend.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.teamAgile.backend.models.AuctionItem;
import com.teamAgile.backend.models.User;
import com.teamAgile.backend.services.AuctionService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/auction")
public class AuctionController {
	
	private final AuctionService auctionService;
	
	public AuctionController(AuctionService auctionService) {
		this.auctionService = auctionService;
	}
	
	@GetMapping("/get-all")
	public ResponseEntity<List<AuctionItem>> getAllUsers(HttpServletRequest request) {
		List<AuctionItem> auctionItems = auctionService.getAllAuctionItems();
		
		HttpSession session = request.getSession(true);
       
        
        System.out.println(session.getAttribute("user"));
        
		
		return ResponseEntity.ok(auctionItems);
	}
	
	@GetMapping("/item")
	public ResponseEntity<AuctionItem> getAuctionItem(@RequestParam String name) {
		System.out.println(name);
		AuctionItem item = auctionService.getAuctionItemByName(name);
		if (item == null) {
	        return ResponseEntity.ok(null);
	    }
		return ResponseEntity.ok(item);
	}
	
	
	@PostMapping("/upload-forward-item")
	public ResponseEntity<AuctionItem> uploadForwardAuctionItem(@RequestBody AuctionItem auctionItem) {
		AuctionItem item = auctionService.createForwardItem(auctionItem);
//		if (createdUser == null) {
//			return ResponseEntity.status(HttpStatus.CONFLICT).body("Username already taken.");
//		}
		return ResponseEntity.status(HttpStatus.CREATED).body(item);
	}
	
	@PostMapping("/upload-dutch-item")
	public ResponseEntity<AuctionItem> uploadDutchAuctionItem(@RequestBody AuctionItem auctionItem) {
		AuctionItem item = auctionService.createDutchItem(auctionItem);
//		if (createdUser == null) {
//			return ResponseEntity.status(HttpStatus.CONFLICT).body("Username already taken.");
//		}
		return ResponseEntity.status(HttpStatus.CREATED).body(item);
	}
	
	
	

}
