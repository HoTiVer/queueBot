package Service;

import Entity.Admin;
import Repository.AdminDao;
import common.ResponseConst;

import java.util.List;
import java.util.Random;

public class AdminService {

    private final AdminDao adminDao;

    public AdminService(AdminDao adminDao) {
        this.adminDao = adminDao;
    }

    public boolean validateQueueAdmin(Long chatId, String userName){
        List<Admin> chatAdmins = adminDao.getChatAdmins(chatId);

        for (var admin : chatAdmins){
            if (admin.getUserName().equals(userName))
                return true;
        }
        return false;
    }

    public boolean validateMainQueueAdmin(Long chatId, String userName){
        List<Admin> chatAdmins = adminDao.getChatAdmins(chatId);

        for (var admin : chatAdmins){
            if (admin.getUserName().equals(userName) && admin.isMainAdmin()){
                return true;
            }
        }
        return false;
    }

    public String getChatAdmins(Long chatId) {
        List<Admin> chatAdmins = adminDao.getChatAdmins(chatId);
        StringBuilder builder = new StringBuilder();
        builder.append("Admins: \n");

        int counter = 0;
        for (var admin : chatAdmins) {
            if (admin.isMainAdmin())
                builder.append("\uD83D\uDC51");
            else {
                builder.append("\uD83D\uDC64");
            }
            builder.append(++counter).append(") ").append(admin.getUserName()).append("\n");
        }

        return builder.toString();
    }


    public String registerAdmin(Long chatId, String requesterName, String userToAdd) {
        List<Admin> chatAdmins = adminDao.getChatAdmins(chatId);

        if (chatAdmins == null || chatAdmins.isEmpty()) {
            Admin admin = Admin.builder()
                    .userName(requesterName)
                    .chatId(chatId)
                    .isMainAdmin(true)
                    .build();
            adminDao.save(admin);
            return requesterName + ResponseConst.ADMIN_NOW;
        }

        Admin mainAdmin = getMainAdmin(chatAdmins, requesterName);
        Admin adminToAdd = getAdminToManipulate(chatAdmins, userToAdd);

        if (adminToAdd != null){
            return userToAdd + " is already admin";
        }
        if (mainAdmin == null){
            return requesterName + ResponseConst.CANNOT_DO_IT;
        }

        Admin admin = Admin.builder()
                .userName(userToAdd)
                .chatId(chatId)
                .isMainAdmin(false)
                .build();

        adminDao.save(admin);
        return "new admin " + userToAdd;
    }

    public String deleteAdmin(Long chatId, String requesterName, String userToRemove) {
        List<Admin> chatAdmins = adminDao.getChatAdmins(chatId);

        Admin mainAdmin = getMainAdmin(chatAdmins, requesterName);
        Admin adminToRemove = getAdminToManipulate(chatAdmins, userToRemove);

        if (requesterName.equals(userToRemove)){
            adminDao.delete(mainAdmin.getId());
            List<Admin> candidates = chatAdmins.stream()
                    .filter(admin -> !admin.getUserName().equals(userToRemove))
                    .toList();

            if (!candidates.isEmpty()) {
                Random random = new Random();
                Admin newMainAdmin = candidates.get(random.nextInt(candidates.size()));

                newMainAdmin.setMainAdmin(true);
                adminDao.update(newMainAdmin);

                return newMainAdmin.getUserName() + ResponseConst.ADMIN_NOW;
            } else {
                return "no admins now.";
            }
        }

        if (adminToRemove == null){
            return "no admins with user: " + userToRemove;
        }
        if (mainAdmin == null){
            return requesterName + ResponseConst.CANNOT_DO_IT;
        }

        adminDao.delete(adminToRemove.getId());
        return userToRemove + " is no longer admin";
    }

    public String raiseMember(Long chatId, String requesterName, String userToRaise) {
        List<Admin> admins = adminDao.getChatAdmins(chatId);

        Admin mainAdmin = getMainAdmin(admins, requesterName);
        Admin adminToRaise = getAdminToManipulate(admins, userToRaise);

        if (mainAdmin != null && adminToRaise != null){
            mainAdmin.setMainAdmin(false);
            adminToRaise.setMainAdmin(true);

            adminDao.update(mainAdmin);
            adminDao.update(adminToRaise);

            return adminToRaise.getUserName() + ResponseConst.ADMIN_NOW;
        }

        return "error";
    }

    private Admin getMainAdmin(List<Admin> admins, String requesterName){
        for (var admin : admins){
            if (admin.getUserName().equals(requesterName) && admin.isMainAdmin()){
                return admin;
            }
        }
        return null;
    }

    private Admin getAdminToManipulate(List<Admin> admins, String adminToManipulate){
        for (var admin : admins){
            if (admin.getUserName().equals(adminToManipulate)){
                return admin;
            }
        }
        return null;
    }
}