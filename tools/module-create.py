#!/usr/bin/env python3

import os
import os.path as path
import argparse
import shutil
import re

TEMPLATE_VAR_PATTERN = re.compile("%(\w+)%")


class Config:
    def __init__(
        self,
        project_root,
        template_root):
        
        self.project_root = project_root
        self.template_root = template_root


def find_project_root():
    for current, dirs, files in walk_up(os.curdir):
        if ("settings.gradle") in files:
            return current

    return None


def walk_up(bottom):
    """ 
    mimic os.walk, but walk 'up'
    instead of down the directory tree
    """

    bottom = path.realpath(bottom)

    #get files in current dir
    try:
        names = os.listdir(bottom)
    except Exception as e:
        print(e)
        return

    dirs, nondirs = [], []
    for name in names:
        if path.isdir(path.join(bottom, name)):
            dirs.append(name)
        else:
            nondirs.append(name)

    yield bottom, dirs, nondirs

    new_path = path.realpath(path.join(bottom, '..'))
    
    # see if we are at the top
    if new_path == bottom:
        return

    for x in walk_up(new_path):
        yield x


def find_and_replace(line, variable, value):
    match = TEMPLATE_VAR_PATTERN.search(line) 
    if match:
        var_name = match.group(1)
        if (var_name == variable):
            line = line.replace("%{}%".format(var_name), value)
        else:
            error("ERROR: unknown variable '{}'".format(var_name))
    
    return line


def patch_module_gradle(target, resource_prefix):
    build_gradle_name = path.join(target, "build.gradle")
    build_gradle_contents = []
    with open(build_gradle_name) as build_gradle:
        build_gradle_contents = build_gradle.readlines()

    with open(build_gradle_name, "w") as build_gradle:
        for line in build_gradle_contents:            
            build_gradle.write(find_and_replace(line, "RESOURCE_PREFIX", resource_prefix))


def patch_module_manifest(target, package_name):
    module_manifest_name = path.join(target, "src", "main", "AndroidManifest.xml")
    with open(module_manifest_name) as manifest:
        contents = manifest.readlines()

    with open(module_manifest_name, "w") as manifest:
        for line in contents:
            manifest.write(find_and_replace(line, "PACKAGE_NAME", package_name))


def find_application_id(build_gradle_name):
    contents = []
    with open(build_gradle_name, "r") as build_gradle:
        contents = build_gradle.readlines() 

    for line in contents:
        if line.find("applicationId") != -1:
            return line.strip().split(" ")[1].replace("\"", "")

    return ""

def create_module(name, configs):
    module_root = path.join(configs.project_root, "modules")
    target = path.join(module_root, name)
    if path.exists(target):
        print("ERROR: the target moulde path is not empty.")
        exit(101)
    template_src = configs.template_root
    # os.makedirs(target)
    shutil.copytree(template_src, target)
    patch_module_gradle(target, name)
    app_build_gradle_name = path.join(configs.project_root, "app", "build.gradle")
    application_id = find_application_id(app_build_gradle_name)
    if not application_id:
        error("Couldn't find applicationId, did you add it correctly?")

    patch_module_manifest(target, application_id)
    print("module {} is created successfully.".format(name))

def error(reason):
    target = path.join(configs.project_root, "modules", args.name)
    shutil.rmtree(target)
    raise RuntimeError(reason)


if __name__ == "__main__":
    project_root = find_project_root()
    if not project_root:
        print("Couldn't find the project file")
        exit(100)
    template_root = path.join(project_root, "template") 
    module_template = path.join(template_root, "module")
    argparse = argparse.ArgumentParser(description="Create the sub module from the template.")
    argparse.add_argument("name", metavar="MODULE_NAME", type=str, help="Module name")
    args = argparse.parse_args()
    if not args.name:
        argparse.print_help()
    else:
        configs = Config(project_root=project_root, template_root=module_template)
        create_module(args.name, configs)
    
