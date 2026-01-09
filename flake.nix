{
  description = "Development environments managed with Nix for practical-database-design";

  inputs = {
    nixpkgs.url = "github:nixos/nixpkgs/nixos-unstable";
    flake-utils.url = "github:numtide/flake-utils";
  };

  outputs = { self, nixpkgs, flake-utils }:
    flake-utils.lib.eachDefaultSystem (system:
      let
        packages = nixpkgs.legacyPackages.${system};
      in
      {
        devShells = {
          # Common
          default = import ./ops/nix/shells/shell.nix { inherit packages; };

          # Languages
          node = import ./ops/nix/environments/node/shell.nix { inherit packages; };
          python = import ./ops/nix/environments/python/shell.nix { inherit packages; };
          java = import ./ops/nix/environments/java/shell.nix { inherit packages; };

          # Applications
          fas = import ./apps/fas/ops/nix/shell.nix { inherit packages; };
          sms = import ./apps/sms/ops/nix/shell.nix { inherit packages; };
        };
      }
    );
}
