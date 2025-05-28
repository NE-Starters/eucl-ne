
package com.eucl.rw.service;

import com.eucl.rw.model.RefreshToken;
import com.eucl.rw.model.User;

public interface RefreshTokenService {
    RefreshToken createRefreshToken(User user);

    RefreshToken validateRefreshToken(String token);

    RefreshToken rotateRefreshToken(RefreshToken oldToken);

    void deleteRefreshToken(String token);
}